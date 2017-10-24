function print(obj) {
	runtime.print(obj);
}

function require(file) {
	if(require.cache[file]) {
		return require.cache[file];
	}
	var code = runtime.getJSFile(file);
	var jsCode = "(function() {" + code + "})()";
	var result = eval(jsCode);
	require.cache[file] = result;
	return require.cache[file];
}
require.cache = {};

function Button(text, x, y, width, height) {
	this.handle = runtime.createButton(text, x, y, width, height);
	this.setClickListener = function(handler) {
		runtime.button_setClickListener(this.handle, handler);
	}
	this.setEnabled = function(enabled) { return this.handle.setEnabled(enabled); }
	this.getText = function() { return this.handle.getText(); }
	this.setText = function(text) { return this.handle.setText(text); }
	this.setVisible = function(visible) { return this.handle.setVisible(visible); }
	this.setTooltip = function(text) { return this.handle.setTooltip(text); }
}

function TextArea(x, y, width, height) {
	this.handle = runtime.createTextArea(x, y, width, height);
	this.getText = function() { return this.handle.getText(); }
	this.setText = function(text) { return this.handle.setText(text); }
	this.setEnabled = function(enabled) { return this.handle.setEnabled(enabled); }
	this.setFocused = function(enabled) { return this.handle.setFocused(enabled); }
	this.setPlaceholder = function(text) { return this.handle.setPlaceholder(text); }
	this.setText = function(text) { return this.handle.setText(text); }
	this.setEditable = function(enabled) { return this.handle.setEditable(enabled); }
	this.setPadding = function(paddig) { return this.handle.setPadding(paddig); }
	this.setVisible = function(visible) { return this.handle.setVisible(visible); }
	this.clear = function() { this.handle.clear(); }
}

function TextField(x, y, width) {
	this.handle = runtime.createTextFeild(x, y, width);
	this.getText = function() { return this.handle.getText(); }
	this.setText = function(text) { return this.handle.setText(text); }
	this.setEnabled = function(enabled) { return this.handle.setEnabled(enabled); }
	this.setFocused = function(enabled) { return this.handle.setFocused(enabled); }
	this.setPlaceholder = function(text) { return this.handle.setPlaceholder(text); }
	this.setText = function(text) { return this.handle.setText(text); }
	this.setEditable = function(enabled) { return this.handle.setEditable(enabled); }
	this.setPadding = function(paddig) { return this.handle.setPadding(paddig); }
	this.setVisible = function(visible) { return this.handle.setVisible(visible); }
	this.clear = function() { this.handle.clear(); }
}

function Label(text, x, y) {
	this.handle = runtime.createLabel(text, x, y);
	this.setText = function(text) { return this.handle.setText(text); }
	this.setVisible = function(visible) { return this.handle.setVisible(visible); }
	this.setScale = function(scale) { return this.handle.setScale(scale); }
	this.setEnabled = function(enabled) { return this.handle.setEnabled(enabled); }
	this.setShadow = function(enabled) { return this.handle.setShadow(enabled); }
	this.setAlignment = function(align) { return this.handle.setAlignment(align); }
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