#!/bin/sh

cd /Applications/MAMP/htdocs/mollify/test/dev_1_8/

rm client/*.cache.*
rm client/*.gwt.rpc
rm client/org.sjarvela.Mollify.nocache.js
rm -rf client/deferredjs/

cp /mollify/build/1_8_7/org.sjarvela.Mollify/*.cache.* client/
cp /mollify/build/1_8_7/org.sjarvela.Mollify/*.gwt.rpc client/
cp /mollify/build/1_8_7/org.sjarvela.Mollify/org.sjarvela.Mollify.nocache.js client/
cp -R /mollify/build/1_8_7/org.sjarvela.Mollify/deferredjs client/