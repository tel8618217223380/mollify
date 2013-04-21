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
	mkdir -p out/js
	mkdir -p out/css
	
	@./node_modules/.bin/jshint js/*.js --config js/.jshintrc
	@echo "Running JSHint on javascript...             ${CHECK} Done"
	@cat js/init.js js/ui.js js/mainview.js js/loginview.js js/uploader.js js/plugins.js > out/js/mollify.src.js
	@./node_modules/.bin/uglifyjs -nc out/js/mollify.src.js > out/js/mollify.min.js
	@echo "Compiling and minifying javascript...       ${CHECK} Done"
	
	./node_modules/.bin/recess --compress css/style.css > out/css/mollify.min.css
	./node_modules/.bin/recess --compress css/bootstrap.css > out/css/bootstrap.css
	./node_modules/.bin/recess --compress css/bootstrap-responsive.css > out/css/bootstrap-responsive.css
	./node_modules/.bin/recess --compress css/font-awesome.css > out/css/font-awesome.css
	./node_modules/.bin/recess --compress css/bootstrap-lightbox.css > out/css/bootstrap-lightbox.css
	
	copy -R css/font out/css
	copy -R css/images out/css
	
	@echo "Compressing CSS...       ${CHECK} Done"
	
	copy -R mollify/backend out
	
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

#
# CLEANS THE ROOT DIRECTORY OF PRIOR BUILDS
#

clean:
	rm -r out

#
# BUILD SIMPLE BOOTSTRAP DIRECTORY
# recess & uglifyjs are required
#

mollify: mollify-css mollify-js


#
# JS COMPILE
#
mollify-js: out/js/*.js

mollify/js/*.js: js/*.js
	mkdir -p out/js
	cat js/init.js js/ui.js js/mainview.js js/loginview.js js/uploader.js js/plugins.js > out/js/mollify.src.js
	./node_modules/.bin/uglifyjs -nc out/js/mollify.src.js > out/js/mollify.min.js

#
# CSS COMPLILE
#

mollify-css: out/css/*.css

mollify/css/*.css: less/*.less
	mkdir -p out/css
	./node_modules/.bin/recess --compile ${BOOTSTRAP_LESS} > out/css/mollify.css
	./node_modules/.bin/recess --compress ${BOOTSTRAP_LESS} > out/css/mollify.min.css
	#./node_modules/.bin/recess --compile ${BOOTSTRAP_RESPONSIVE_LESS} > out/css/bootstrap-responsive.css
	#./node_modules/.bin/recess --compress ${BOOTSTRAP_RESPONSIVE_LESS} > bootstrap/css/bootstrap-responsive.min.css

#
# WATCH LESS FILES
#

watch:
	echo "Watching less files..."; \
	watchr -e "watch('less/.*\.less') { system 'make' }"


.PHONY: watch mollify-css mollify-js