LIBDIR = /usr/local/lib/java
SRCDIR = .
INSTALLDIR = install

JARSPATH = $(LIBDIR)/jakarta-oro-2.0.2-dev-2.jar:$(LIBDIR)/jdbc2_0-stdext.jar:$(LIBDIR)/Opta2000.jar:$(LIBDIR)/jsdk.jar:$(LIBDIR)/jcchart400K.jar
IMCMSCLASSPATH = /home/kreiger/imCMS1.3/1.3/server/classes
DIRSCLASSPATH = $(SRCDIR):$(SRCDIR)/diagram:$(SRCDIR)/conf
CLASSPATH = $(JARSPATH):$(IMCMSCLASSPATH):$(DIRSCLASSPATH)

COMPILER = javac
VM = java
COPTIONS = -g -deprecation
ROPTIONS =

CLASSES = AddDoc.class \
          AdminDoc.class \
          BackDoc.class \
          ChangeExternalDoc2.class \
          ChangeImage.class \
          ChangeText.class \
          FileAdmin.class \
          GenericUpload.class \
          GetDoc.class \
          GetExistingDoc.class \
          ImageBrowse.class \
          ImageUpload.class \
          LastRequest.class \
          LogOut.class \
          MetaAdmin.class \
          Restart.class \
          SaveBrowserDoc.class \
          SaveFileUpload.class \
          SaveFrameset.class \
          SaveImage.class \
          SaveInclude.class \
          SaveInPage.class \
          SaveMeta.class \
          SaveNewBrowserDoc.class \
          SaveNewFileUpload.class \
          SaveNewFrameset.class \
          SaveNewMeta.class \
          SaveNewUrlDoc.class \
          SavePermissions.class \
          SaveSort.class \
          SaveText.class \
          SaveUrlDoc.class \
          SearchDocuments.class \
          ShowDiagram.class \
          StartDoc.class \
          TemplateAdd.class \
          TemplateAdmin.class \
          TemplateChange.class \
          UrlDocTest.class \
          VerifyUser.class \
          \
          AdminCounter.class \
          AdminDeleteDoc.class \
          AdminError.class \
          AdminError2.class \
          AdminIpAccess.class \
          AdminListDocs.class \
          AdminManager.class \
          AdminRoleBelongings.class \
          AdminRoles.class \
          AdminSystemInfo.class \
          AdminUser.class \
          AdminUserPhones.class \
          AdminUserProps.class \
          Administrator.class \
          ErrorMessageGenerator.class \
          PasswordMailReminder.class \
          UserChangePrefs.class \
          UserHandler.class \
          \
          diagram/ChangeDiagram.class \
          diagram/ChangeDiagram1.class \
          diagram/ChangeDiagramCoordinator.class \
          diagram/DiagramChanger.class \
          diagram/DiagramCreator.class \
          diagram/DiagramManager.class \
          diagram/DiagramViewer.class \
          diagram/Error.class \
          diagram/NewDiagram.class \
          diagram/NewDiagramCoordinator.class \
          diagram/ViewDiagram1.class \
          \
          conf/AdminConference.class \
          conf/ConfAdd.class \
          conf/ConfAdmin.class \
          conf/ConfCreator.class \
          conf/ConfDisc.class \
          conf/ConfDiscView.class \
          conf/ConfError.class \
          conf/ConfForum.class \
          conf/ConfHelp.class \
          conf/ConfLogin.class \
          conf/ConfManager.class \
          conf/ConfReply.class \
          conf/ConfViewer.class \
          conf/Conference.class \
          \

INSTALLED = $(addprefix $(INSTALLDIR)/,$(notdir $(CLASSES)))

.SUFFIXES: .java .class

.PHONY: all jar clean install

all : $(CLASSES)

jar : all
	jar cvf imcms-servlets.jar $(CLASSES)

install : $(INSTALLED)

$(INSTALLDIR)/%.class : %.class
	cp $? $@

# Remove all class files from the project directory.
clean:
	$(RM) $(CLASSES)

# Implicit rule for making Java class files from Java
# source files.
.java.class:
	$(COMPILER) $? $(COPTIONS) -classpath "$(CLASSPATH)"












