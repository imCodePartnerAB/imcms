#***************************************************************************
#
#  Section 1: Directories.
#
#***************************************************************************

CLASS_DIR        = $(JAVA_DEV_ROOT)/classes
DOC_DIR          = $(JAVA_DEV_ROOT)/docs
MAKE_DIR         = $(JAVA_DEV_ROOT)/make
JAR_DIR          = $(JAVA_DEV_ROOT)/jars

ifdef JAVA_DEV_LOCAL
SOURCE_DIR       = $(JAVA_DEV_LOCAL)/src
LOCAL_CLASS_DIR  = $(JAVA_DEV_LOCAL)/classes
DESTINATION      = $(JAVA_DEV_LOCAL)/classes
else
SOURCE_DIR       = $(JAVA_DEV_ROOT)/src
DESTINATION      = $(JAVA_DEV_ROOT)/classes
endif

JAVA_HOME        = /usr
JAVA_BIN         = $(JAVA_HOME)/bin

#***************************************************************************
#
#  Section 2. Tools and options.
#
#***************************************************************************

JAVA     = java
JAVAC    = javac
JAVAH    = javah
RMIC     = rmic
JAR      = jar
DEBUG    = jdb
DELETE   = rm -f
COPY     = cp
JINDENT  = Jindent
PROFILER = -Xrunhprof
MAKEDIR  = mkdir -p
PRINT    = @echo
JAVADOC  = javadoc
CHMOD    = chmod

# Change if not unix
SEP = :

EMPTY            =
SPACE            = $(EMPTY) $(EMPTY)

LOCAL_JARTMP     = $(patsubst %,$(JAR_DIR)/%,$(JARS))
LOCAL_JARLIST    = $(subst $(SPACE),$(SEP),$(LOCAL_JARTMP))

OTHER_JARTMP     = $(patsubst %,$(JAR_DIR)/%,$(JARS_3RDPARTY))
OTHER_JARLIST    = $(subst $(SPACE),$(SEP),$(OTHER_JARTMP))

JRE              = $(JAVA_HOME)/jre/lib/rt.jar

SOURCEPATH       = $(SOURCE_DIR)
CLASSPATH        = $(INCLUDES)$(SEP)$(JRE)$(SEP)$(LOCAL_CLASS_DIR)$(SEP)$(CLASS_DIR)$(SEP)$(LOCAL_JARLIST)$(SEP)$(OTHER_JARLIST)

JAVAC_OPTIONS    = -d $(DESTINATION) -classpath $(CLASSPATH) -sourcepath $(SOURCEPATH) -deprecation
JAVA_OPTIONS     = -classpath $(CLASSPATH) 
RMIC_OPTIONS     = -d $(CLASS_DIR) -classpath $(CLASSPATH)
JAR_OPTIONS      = -cvmf
JINDENT_OPTIONS  = -p $(MAKE_DIR)/style.jin
PROFILER_OPTIONS = cpu=samples,depth=6

JAVADOC_OPTIONS  = \
	-d $(DOC_DIR) \
	-sourcepath $(SOURCE_DIR) \
	-classpath $(CLASSPATH) \
	-author \
	-package \
	-use \
	-splitIndex \
	-version \
	-windowtitle $(WINDOWTITLE) \
	-doctitle $(DOCTITLE) \
	-header $(HEADER) \
	-bottom $(BOTTOM)



#***************************************************************************
#
#  Section 3. Rules and dependencies.
#
#  This section defines the exact rules for creating a target file from
#  a (set of) source file(s). The rules can be quite complex and the
#  makefile syntax is not extreamly readable. A quick crash course:
#
#  target : depends
#    rule
#
#  target  - the parameter given to make: What to build
#  depends - file or other targets target depends on
#  rule    - how to create target
#  $(VAR)  - environment variable or variable defined above
#  $@      - Current target
#  $*      - Current target without extension
#  $< - Current dependency
#
#***************************************************************************


PACKAGE_LOC     = $(subst .,/,$(PACKAGE))
PACKAGE_DIR     = $(DESTINATION)/$(PACKAGE_LOC)
JAVA_FILES      = $(filter  %.java,$(SOURCE))
NONJAVA_FILES   = $(patsubst %.java,,$(SOURCE))
CLASS_FILES     = $(JAVA_FILES:%.java=$(PACKAGE_DIR)/%.class)
OTHER_FILES     = $(NONJAVA_FILES:%=$(PACKAGE_DIR)/%)
JNI_CLASS_FILES = $(JNI_SOURCE:%.java=$(PACKAGE_DIR)/%.class)
JNI_HEADERS     = $(JNI_SOURCE:%.java=%.h)
RMI_CLASS_FILES = $(RMI_SOURCE:%.java=$(PACKAGE_DIR)/%.class)
RMI_STUB_FILES  = $(RMI_SOURCE:%.java=$(PACKAGE_DIR)/%_Stub.class)
RMI_SKEL_FILES  = $(RMI_SOURCE:%.java=$(PACKAGE_DIR)/%_Skel.class)
ALL_CLASS_FILES = $(CLASS_FILES) $(RMI_STUB_FILES) $(RMI_SKEL_FILES)
JAR_CONTENT_CMD = $(patsubst %,-C $(CLASS_DIR) %,$(JAR_CONTENT))


# Make a list of all packages involved
ifdef PACKAGE
PACKAGE_LIST    = $(subst .,/,$(PACKAGE))
MAIN_CLASS      = $(MAIN)
MAIN_PACKAGE    = $(PACKAGE)
else
PACKAGE_LIST    = $(subst .,/,$(PACKAGES)) $(subst .,/,$(NODOC_PACKAGES))
endif

PLIST_CLEAN     = $(patsubst %,$(SOURCE_DIR)/%/.clean,$(PACKAGE_LIST))
PLIST_BUILD     = $(patsubst %,$(SOURCE_DIR)/%/.build,$(PACKAGE_LIST))


# Rule 0. Applied when make is called without targets. Invokes rule 10.
default : buildall


# Rule 1. Building a .class file from a .java file
$(PACKAGE_DIR)/%.class :: $(SOURCE_DIR)/$(PACKAGE_LOC)/%.java
	$(JAVA_BIN)/$(JAVAC) $(JAVAC_OPTIONS) $< 
# Rule 2. Building a .class file from a .java file. Invokes rule 1.
%.class : $(SOURCE_DIR)/$(PACKAGE_LOC)/%.java
	$(MAKE) -k $(PACKAGE_DIR)/$@


# Rule 3. Building a JNI .h stub file from a .class file
$(SOURCE_DIR)/$(PACKAGE_LOC)/%.h : $(PACKAGE_DIR)/%.class
	$(JAVA_BIN)/$(JAVAH) $(JAVAH_OPTIONS) $(PACKAGE).$*


# Rule 4. Building a JNI .h stub file from a class file. Invokes rule 3.
%.h : %.class
	$(MAKE) -k $(SOURCE_DIR)/$(PACKAGE_LOC)/$@


# Rule 5. Building an RMI _Stub.class file from a .class file 
$(PACKAGE_DIR)/%_Stub.class :: $(PACKAGE_DIR)/%.class
	$(JAVA_BIN)/$(RMIC) $(RMIC_OPTIONS) $(PACKAGE).$*


# Rule 6. Building an RMI _Skel.class file from a .class file
$(PACKAGE_DIR)/%_Skel.class :: $(PACKAGE_DIR)/%.class
	$(JAVA_BIN)/$(RMIC) $(RMIC_OPTIONS) $(PACKAGE).$*


# Rule 7. Building an RMI _Stub.class file from a .class file. Invokes rule 5.
%_Stub.class : %.class
	$(MAKE) -k $(PACKAGE_DIR)/$@


# Rule 8. Building an RMI _Skel.class file from a .class file. Invokes rule 6.
%_Skel.class : %.class
	$(MAKE) -k $(PACKAGE_DIR)/$@


# Rule 9. Default behaviour within a package: Simply copy the object from src
# to classes. Note that the location of this rule is important. It must be after
# the package specifics.
$(PACKAGE_DIR)/% :: $(SOURCE_DIR)/$(PACKAGE_LOC)/%
#	$(MAKEDIR) $(PACKAGE_DIR)
	$(COPY) $< $@
	$(CHMOD) u+rw $@


# Rule 10. Build class files rmi stub and skeletons and process all other source
all : $(CLASS_FILES) $(RMI_STUB_FILES) $(RMI_SKEL_FILES) $(OTHER_FILES)


# Rule 11. Build JNI .h files. Invokes rule 4.
jni       : $(JNI_CLASS_FILES) $(JNI_HEADERS)


# Rule 12. Build RMI stubs and skeleton files. Invokes rule 7. and rule 8.
rmi       : $(RMI_CLASS_FILES) $(RMI_STUB_FILES) $(RMI_SKEL_FILES)


# Rule 13. Remove all produced files (except javadoc)
cleanall :
	$(DELETE) $(PACKAGE_DIR)/*.class $(OTHER_FILES) $(JNI_HEADERS)


# Rule 14. Change ".clean" tag to "Makefile", thus call the package makefile which
# in turn recalls this makefile with target cleanall (rule 13).
%.clean :
	$(MAKE) -k -f $(subst .clean,Makefile,$@) cleanall


# Rule 15: Call rule 14 for every package directory
clean : $(PLIST_CLEAN)
	$(PRINT) Done clean.


# Rule 16. Change ".build" tag to "Makefile", thus call the package makefile which
# in turn recalls this makefile with target all (rule 10).
%.build :
	$(MAKE) -k -f $(subst .build,Makefile,$@) all


# Rule 17. Call rule 16 for every package
buildall : $(PLIST_BUILD)		
	$(PRINT) Done build.


# Rule 18. Build a jar file. $* strips the last phony .JAR extension.
%.JAR :
	$(DELETE) $(JAR_DIR)/$*
	$(JAVA_BIN)/$(JAR) $(JAR_OPTIONS) $(JAR_DIR)/$(MANIFEST) \
		$(JAR_DIR)/$* $(JAR_CONTENT_CMD)
#	$(JAVA_BIN)/$(JAR) -i $(JAR_DIR)/$@


# Rule 19. Create given jar file by invoking its Makefile which triggers rule 18
%.jar :
	$(MAKE) -k -f $(patsubst %,$(JAR_DIR)/Makefile.%,$@) $@.JAR


# Rule 20. Create all jar files by invoking rule 19
jar : $(JARS)
	$(PRINT) Done jars.


# Rule 21. Build javadoc for all listed packages
javadoc :
	$(PRINT) $(PACKAGES) > $(JAVA_DEV_ROOT)/packages.tmp
	$(JAVA_BIN)/$(JAVADOC) $(JAVADOC_OPTIONS) @$(JAVA_DEV_ROOT)/packages.tmp
	$(DELETE) $(JAVA_DEV_ROOT)/packages.tmp
	$(PRINT) Done JavaDoc.


# Rule 22. Run application using classes tree
run :
	$(JAVA_BIN)/$(JAVA) $(JAVA_OPTIONS) $(MAIN_PACKAGE).$(MAIN_CLASS) \
		$(RUN_PARAMETERS)


# Rule 23. Run application using jar archive
runjar :
	$(JAVA_BIN)/$(JAVA) -jar $(JAR_DIR)/$(MAIN_JAR) $(RUN_PARAMETERS)


# Rule 24. Run debugger
debug :
	$(JAVA_BIN)/$(DEBUG) $(JAVA_OPTIONS) $(PACKAGE).$(MAIN)


# Rule 25. Run the auto indentation tool. Experimental setup. 
indent :
	$(JAVA_BIN)/$(JAVA) -classpath $(JAR_3RDPARY_DIR)/Jindent.jar \
		$(JINDENT) $(JINDENT_OPTIONS) -f "*.java"


# Rule 26. Run profiler.
profile :
	$(JAVA_BIN)/$(JAVA) $(PROFILER):$(PROFILER_OPTIONS) $(JAVA_OPTIONS) \
		$(PACKAGE).$(MAIN)


# Rule 27. A combination of steps used for automatic building
complete : clean buildall jar javadoc
