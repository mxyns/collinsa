mkdir build
javac -encoding UTF-8 -d ./build -sourcepath src src/fr/insalyon/mxyns/collinsa/Collinsa.java
echo Class-Path: fr res  > build/manifest.txt
jar cvfme Collinsa.jar build/manifest.txt fr.insalyon.mxyns.collinsa.Collinsa -C build fr -C res .
rmdir /S /Q build