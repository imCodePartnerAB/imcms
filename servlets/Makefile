CLASSPATH = 	/usr/local/java/jakarta-oro-2.0.2-dev-2.jar:/usr/local/java/jdbc2_0-stdext.jar:/usr/local/java/Opta2000.jar:/usr/local/java/JSDK2.0/lib/jsdk.jar:/home/kreiger/imCMS1.3/1.3/server/classes:/usr/local/java/jcchart400K.jar:.:diagram/:conf/

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
          ChangeUrl.class \
          FileAdmin.class \
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

.SUFFIXES: .java .class

all : $(CLASSES)

jar : all
	jar cvf imcms-servlets.jar *.class

# Remove all class files from the project directory.
clean:
	rm *.class

# Implicit rule for making Java class files from Java
# source files.
.java.class:
	$(COMPILER) $(COPTIONS) -classpath "$(CLASSPATH)"  $?












