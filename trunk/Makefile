BOOTSTRAP = ./docs/assets/css/bootstrap.css
BOOTSTRAP_LESS = ./less/bootstrap.less
BOOTSTRAP_RESPONSIVE = ./docs/assets/css/bootstrap-responsive.css
BOOTSTRAP_RESPONSIVE_LESS = ./less/responsive.less
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
	mkdir -p out/src/js
	@./node_modules/.bin/jshint js/*.js --config js/.jshintrc
	#@./node_modules/.bin/jshint js/tests/unit/*.js --config js/.jshintrc
	@echo "Running JSHint on javascript...             ${CHECK} Done"
	#@./node_modules/.bin/recess --compile ${BOOTSTRAP_LESS} > ${BOOTSTRAP}
	#@./node_modules/.bin/recess --compile ${BOOTSTRAP_RESPONSIVE_LESS} > ${BOOTSTRAP_RESPONSIVE}
	#@echo "Compiling LESS with Recess...               ${CHECK} Done"
	#@node docs/build
	#@cp img/* docs/assets/img/
	#@cp js/*.js docs/assets/js/
	#@cp js/tests/vendor/jquery.js docs/assets/js/
	#@echo "Compiling documentation...                  ${CHECK} Done"
	@cat js/init.js js/main.js js/uploader.js > out/src/js/mollify.tmp.js
	@./node_modules/.bin/uglifyjs -nc out/src/jsmollify.tmp.js > out/src/js/mollify.min.js
	#@echo "/**\n* Bootstrap.js v2.3.1 by @fat & @mdo\n* Copyright 2012 Twitter, Inc.\n* http://www.apache.org/licenses/LICENSE-2.0.txt\n*/" > docs/assets/js/copyright.js
	#@cat docs/assets/js/copyright.js docs/assets/js/bootstrap.min.tmp.js > docs/assets/js/bootstrap.min.js
	@rm out/src/js/mollify.tmp.js
	@echo "Compiling and minifying javascript...       ${CHECK} Done"
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
	mkdir -p out/deploy/js
	cat js/init.js js/main.js js/uploader.js > out/deploy/js/mollify.tmp.js
	./node_modules/.bin/uglifyjs -nc out/deploy/js/mollify.tmp.js > out/deploy/js/mollify.min.js
	#echo "/*!\n* Bootstrap.js by @fat & @mdo\n* Copyright 2012 Twitter, Inc.\n* http://www.apache.org/licenses/LICENSE-2.0.txt\n*/" > bootstrap/js/copyright.js
	#cat bootstrap/js/copyright.js bootstrap/js/bootstrap.min.tmp.js > bootstrap/js/bootstrap.min.js
	rm out/deploy/js/mollify.min.tmp.js

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