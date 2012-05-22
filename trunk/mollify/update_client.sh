#!/bin/sh

cd /Applications/MAMP/htdocs/mollify/test/dev/

rm client/*.cache.*
rm client/*.gwt.rpc
rm client/org.sjarvela.Mollify.nocache.js
rm -rf client/deferredjs/

cp /mollify/build/trunk/org.sjarvela.Mollify/*.cache.* client/
cp /mollify/build/trunk/org.sjarvela.Mollify/*.gwt.rpc client/
cp /mollify/build/trunk/org.sjarvela.Mollify/org.sjarvela.Mollify.nocache.js client/
cp -R /mollify/build/trunk/org.sjarvela.Mollify/deferredjs client/