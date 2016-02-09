You need to install Notes.jar to your local maven repository in order to be able to use maven:

cd libs_to_import

mvn install:install-file -Dfile=Notes.jar -DgroupId=lotus.notes -DartifactId=lotus.notes -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=ical4j.jar -DgroupId=net.fortuna -DartifactId=ical4j -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true