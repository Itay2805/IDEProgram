package me.itay.idemodthingy.programs.bluej.api.comp;

import me.itay.idemodthingy.programs.bluej.resources.BlueJResourceLocation;

public class ComboBox {

    public static class List<T> extends com.mrcrayfish.device.api.app.component.ComboBox.List<T> implements Component{
        private String name;
        private BlueJResourceLocation resloc;

        public List(int left, int top, T[] items, String name, BlueJResourceLocation resloc) {
            super(left, top, items);
            this.name = name;
            this.resloc = resloc;
        }

        public List(int left, int top, int width, T[] items, String name, BlueJResourceLocation resloc) {
            super(left, top, width, items);
            this.name = name;
            this.resloc = resloc;
        }

        public List(int left, int top, int comboBoxWidth, int listWidth, T[] items, String name, BlueJResourceLocation resloc) {
            super(left, top, comboBoxWidth, listWidth, items);
            this.name = name;
            this.resloc = resloc;
        }
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public BlueJResourceLocation getResourceLocation() {
            return this.resloc;
        }
    }

    public static class Custom<T> extends com.mrcrayfish.device.api.app.component.ComboBox.Custom<T> implements Component{

        private String name;
        private BlueJResourceLocation resloc;

        public Custom(int left, int top, int width, int contextWidth, int contextHeight, String name, BlueJResourceLocation resloc) {
            super(left, top, width, contextWidth, contextHeight);
            this.name = name;
            this.resloc = resloc;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public BlueJResourceLocation getResourceLocation() {
            return this.resloc;
        }
    }
}
