function initEventLogging() {
	return {
		views: [
			{header:"Events", id:'menu-header-events', views: [
				{title:"All", id:'menu-events-all', "class" : "MollifyEventsView", "script" : "events.js", "title": "All Events"},
				{title:"Downloads", id:'menu-events-downloads', "class" : "MollifyDownloadsView", "script" : "downloads.js", "title": "Downloads"}
			]}
		]
	};
}