DATE=$(shell date +%I:%M%p)
CHECK=\033[32m✔\033[39m
HR=\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#\#


#
# BUILD
#

build:
	@echo "\n${HR}"
	@echo "Building Mollify..."
	@echo "${HR}\n"
	rm -rf out
	mkdir -p out/mollify/js
	mkdir -p out/mollify/css
	
	@./node_modules/.bin/jshint js/*.js --config js/.jshintrc
	@echo "Running JSHint on javascript...             ${CHECK} Done"
	@cat js/init.js js/ui.js js/mainview.js js/loginview.js js/uploader.js js/plugins.js > out/mollify/js/mollify.src.js
	@./node_modules/.bin/uglifyjs -nc out/mollify/js/mollify.src.js > out/mollify/js/mollify.min.js
	@echo "Compiling and minifying javascript...       ${CHECK} Done"
	
	./node_modules/.bin/recess --compress css/style.css > out/mollify/css/mollify.min.css
	./node_modules/.bin/recess --compress css/bootstrap.css > out/mollify/css/bootstrap.css
	./node_modules/.bin/recess --compress css/bootstrap-responsive.css > out/mollify/css/bootstrap-responsive.css
	./node_modules/.bin/recess --compress css/font-awesome.css > out/mollify/css/font-awesome.css
	./node_modules/.bin/recess --compress css/bootstrap-lightbox.css > out/mollify/css/bootstrap-lightbox.css
	
	cp -R css/font out/mollify/css
	cp -R css/images out/mollify/css
	
	@echo "Compressing CSS...       ${CHECK} Done"
	
	cp -R mollify/backend out/mollify/
	#remove unnecessary/excluded resources
	find out/mollify -name '.svn' | xargs rm -rf
	rm out/mollify/backend/configuration.php
	rm -rf out/mollify/backend/dav
	rm -rf out/mollify/backend/db.*
	rm -rf out/mollify/backend/admin/settings.js
	rm -rf out/mollify/backend/admin/custom/*
	rm -rf out/mollify/backend/plugin/S3
	rm -rf out/mollify/backend/plugin/Plupload
	rm -rf out/mollify/backend/FileViewerEditor/viewers/FlowPlayer
	rm -rf out/mollify/backend/FileViewerEditor/viewers/JPlayer
	rm -rf out/mollify/backend/FileViewerEditor/viewers/TextFile
	rm -rf out/mollify/backend/FileViewerEditor/viewers/Zoho
	rm -rf out/mollify/backend/FileViewerEditor/viewers/FlexPaper
	rm -rf out/mollify/backend/FileViewerEditor/viewers/CKEditor
	
	cp out/mollify/backend/example/example_index.html out/mollify/index.html
	@echo "Backend...       ${CHECK} Done"
	
	zip -r out/mollify.zip out/mollify
	
	@echo "\n${HR}"

#
# RUN JSHINT & QUNIT TESTS IN PHANTOMJS
#

test:
	#./node_modules/.bin/jshint js/*.js --config js/.jshintrc
	#./node_modules/.bin/jshint js/tests/unit/*.js --config js/.jshintrc
	#node js/tests/server.js &
	#phantomjs js/tests/phantom.js "http://localhost:3000/js/tests"
	#kill -9 `cat js/tests/pid.txt`
	#rm js/tests/pid.txt

clean:
	rm -rf out

#
# WATCH LESS FILES
#

watch:
	echo "Watching less files..."; \
	watchr -e "watch('less/.*\.less') { system 'make' }"


.PHONY: watch