package com.imcode
package imcms.admin.system.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import scala.collection.mutable.{Map => MMap}
import com.vaadin.terminal.{Resource}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.data.util.FilesystemContainer
import java.io.{FilenameFilter, File}
import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher
import java.util.{Date}

/** Hierarchical filesystem (non-hidden dirs) container with a single root. */
class LocationTreeContainer(root: File) extends FilesystemContainer(root) {

  import java.util.Collections._

  setFilter(new FilenameFilter {
    def accept(file: File, name: String) = let(new File(file, name)) { fsNode =>
      fsNode.isDirectory && !fsNode.isHidden
    }
  })

  override def rootItemIds() = root |> singleton[File] |> unmodifiableCollection[File]

  override def addRoot(root: File) = error("Operation is not allowed.")
}


/** Predefined dir items filters. */
object LocationItemsFilter {

  import scala.util.matching.Regex

  /** Creates a compund filter from a sequence of filters. */
  def apply(filter: File => Boolean, filters: File => Boolean*) = (file: File) => filter +: filters forall { _ apply file }

  val notHidden = apply(!_.isHidden)

  //val fileOnly = apply(notHidden, _.isFile)

  def nameRE(re: Regex)(fsNode: File) = re.unapplySeq(fsNode.getName).isDefined

  def fileWithExt(ext: String, exts: String*) = nameRE("""(?i).*\.(%s)""".format(ext +: exts mkString("|")).r)_

  val imageFile = fileWithExt("png", "gif", "jpg", "jpeg")

  val templateFile = fileWithExt("jsp", "jspx", "html")
}


/** File browser location (bookmark) conf. */
case class LocationConf(root: File, itemsFilter: File => Boolean = LocationItemsFilter.notHidden, recursive: Boolean = true)


case class LocationSelection(dir: File, items: Seq[File]) {
  def firstItem = items.headOption
  def hasItems = items.nonEmpty
  def hasSingleItem = items.size == 1
}


/**
 * A file browser can have any number of locations (bookmarks).
 * A location is uniquely identified by its root dir.
 */
class FileBrowser(val isSelectable: Boolean = true, val isMultiSelect: Boolean = false)
    extends Publisher[Option[LocationSelection]] {

  type Location = (LocationTree, LocationItems)
  type Tab = Component // location tree ui

  private val tabsToLocations = MMap.empty[Tab, Location]

  /** Current (visible) location. */
  private val locationRef = new AtomicReference(Option.empty[Location])

  /** Selection in a current location */
  private val selectionRef = new AtomicReference(Option.empty[LocationSelection])

  val ui = letret(new FileBrowserUI) { ui =>
    ui.accLocationTrees.addListener(new TabSheet.SelectedTabChangeListener {
      def selectedTabChange(e: TabSheet#SelectedTabChangeEvent) {
        val locationOpt = tabsToLocations.get(e.getTabSheet.getSelectedTab)
        locationRef.set(locationOpt)

        for ((locationTree, locationItems) <- locationOpt) {
          ui.setSecondComponent(locationItems.ui)
          updateSelection(locationTree, locationItems)
        }
      }
    })
  }

  def addLocation(caption: String, conf: LocationConf, icon: Option[Resource] = None) {
    val locationTree = new LocationTree(conf.root)
    val locationItems = new LocationItems(conf.itemsFilter, isSelectable, isMultiSelect)

    locationTree.ui addValueChangeHandler {
      ?(locationTree.ui.value) match {
        case Some(dir) =>
          locationItems.reload(dir)
          let(Some(LocationSelection(dir, Nil))) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }

        case _ =>
          locationItems.ui.removeAllItems()

          let(None) { selection =>
            selectionRef.set(selection)
            notifyListeners(selection)
          }
      }
    }

    locationItems.ui addValueChangeHandler { updateSelection(locationTree, locationItems) }

    locationItems.ui.addItemClickListener {
      case e if e.isDoubleClick => e.getItemId match {
        case item: File if item.isDirectory => locationTree.selection = item
        case _ =>
      }

      case _ =>
    }

    locationTree.reload()

    tabsToLocations(locationTree.ui) = (locationTree, locationItems)
    ui.accLocationTrees.addTab(locationTree.ui, caption, icon.orNull)
  }

  private def updateSelection(locationTree: LocationTree, locationItems: LocationItems) {
    ?(locationTree.ui.value) match {
      case Some(dir) =>
        val items = if (isMultiSelect) locationItems.ui.asInstanceOf[MultiSelect2[File]].value.toSeq
                    else ?(locationItems.ui.asInstanceOf[SingleSelect2[File]].value).toSeq

        let(Some(LocationSelection(dir, items))) { selection =>
          selectionRef.set(selection)
          notifyListeners(selection)
        }

      case _ =>
        let(None) { selection =>
          selectionRef.set(selection)
          notifyListeners(selection)
        }
    }
  }

  override def notifyListeners() = notifyListeners(selection)

  /** Returns selection in a current location. */
  def selection = selectionRef.get

  /** Returns current (visible) location */
  def location = locationRef.get

  def locations: Map[File, Location] =
    tabsToLocations.values map {
      case loc @ (locationTree, _) => locationTree.root.getCanonicalFile -> loc
    } toMap

  /** Returns location by its root. */
  def location(root: File): Option[Location] = locations.get(root.getCanonicalFile)

  /** Reloads current location. */
  def reloadLocation(preserveTreeSelection: Boolean = true) =
    for ((locationTree, _) <- location; dir = locationTree.ui.value) {
      locationTree.reload()
      if (preserveTreeSelection && dir.isDirectory) locationTree.selection = dir
    }

  /** Reloads current location's items. */
  def reloadLocationItems() =
    for ((locationTree, locationItems) <- location; dir = locationTree.ui.value)
      locationItems.reload(dir)


  /**
   * Changes current selection.
   * Also changes current location if its root is other than provided locationRoot.
   */
  def select(locationRoot: File, dir: File, items: Seq[File] = Nil): Unit =
    select(locationRoot, new LocationSelection(dir, items))

  /**
   * Changes current selection.
   * Also changes current location if its root is other than provided locationRoot.
   */
  def select(locationRoot: File, locationSelection: LocationSelection) =
    tabsToLocations.find {
      case (_, (locationTree, _)) => locationTree.root.getCanonicalFile == locationRoot.getCanonicalFile
    } foreach {
      case (tab, (locationTree, locationItems)) =>
        ui.accLocationTrees.setSelectedTab(tab)
        locationTree.selection = locationSelection.dir
        if (isSelectable && locationSelection.hasItems) {
          locationItems.ui.setValue(if (isMultiSelect) asJavaCollection(locationSelection.items) else locationSelection.firstItem.get)
        }
    }
}


class FileBrowserUI extends HorizontalSplitPanel with FullSize {
  val accLocationTrees = new Accordion with FullSize

  setFirstComponent(accLocationTrees)
  setSplitPosition(15)
}


trait FSItemIcon extends AbstractSelect {
  override def getItemIcon(itemId: AnyRef) = itemId.asInstanceOf[File] match {
    case item if item.isDirectory => Theme.Icons.Folder16
    case _ => Theme.Icons.File16
  }
}


class LocationTree(val root: File) {
  val ui = new Tree with SingleSelect2[File] with Immediate with NoNullSelection with FSItemIcon

  def reload() {
    ui.setContainerDataSource(new LocationTreeContainer(root.getCanonicalFile))
    ui.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_ITEM)
    selection = root
  }

  def selection_=(dir: File) = let(dir.getCanonicalFile) { dir =>
    ui.select(dir)
    ui.expandItem(if (dir == root) dir else dir.getParentFile)
  }

  def selection = ui.value
}


class LocationItems(filter: File => Boolean, selectable: Boolean, multiSelect: Boolean) {

  val ui = letret(if (multiSelect) new Table with MultiSelect2[File] with FSItemIcon
                  else             new Table with SingleSelect2[File] with FSItemIcon) { ui =>

    ui.setSizeFull
    ui.setImmediate(true)
    ui.setSelectable(selectable)

    addContainerProperties(ui,
      CP[String]("Name"),
      CP[Date]("Date modified"),
      CP[String]("Size"),
      CP[String]("Kind"))

    import Table._
    ui.setColumnAlignments(Array(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT))
    ui.setRowHeaderMode(ROW_HEADER_MODE_ICON_ONLY);
  }

  /** Populates table with dir items. */
  def reload(dir: File) {
    val base = 1024
    val baseFn = java.lang.Math.pow(1024, _:Int).toInt
    val (dirs, files) = dir.listFiles.partition(_.isDirectory)

    ui.removeAllItems()

    dirs.sortWith((d1, d2) => d1.getName.compareToIgnoreCase(d2.getName) < 0) foreach { dir =>
      ui.addItem(Array[AnyRef](dir.getName, new Date(dir.lastModified), "--", "Folder"), dir)
    }

    for (file <- files.sortWith((f1, f2) => f1.getName.compareToIgnoreCase(f2.getName) < 0) if filter(file)) {
      val (size, units) = file.length match {
        case size if size < baseFn(1) => (size, "--")
        case size if size < baseFn(2) => (size / base, "KB")
        case size if size < baseFn(3) => (size / base, "MB")
        case size => (size / base, "GB")
      }

      ui.addItem(Array[AnyRef](file.getName, new Date(file.lastModified), "%d %s".format(size, units), "File"), file)
    }
  }
}