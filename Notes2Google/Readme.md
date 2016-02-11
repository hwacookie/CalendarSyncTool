You need to install some jar files into your local maven repository because they are not available in any maven repository.


cd libs_to_import

mvn install:install-file -Dfile=Notes.jar -DgroupId=lotus.notes -DartifactId=lotus.notes -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true

mvn install:install-file -Dfile=ical4j.jar -DgroupId=net.fortuna -DartifactId=ical4j -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true