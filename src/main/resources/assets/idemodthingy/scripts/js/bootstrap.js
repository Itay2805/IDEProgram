var print = function(obj) {
	runtime.print(obj);
}

function Button(text, x, y, width, height) {
	this.handle = runtime.createButton(text, x, y, width, height);
	this.setClickListener = function(handler) {
		runtime.button_setClickListener(this.handle, handler);
	}
}

var app = {};

app.setOnRender = function(handler) {
	runtime.onRender(handler);
}

app.addComponent = function(comp) {
	runtime.addComponent(comp.handle);
}

app.message = function(text, title) {
	if(!title) title = "Message";
	runtime.message(text, title);
}