package me.itay.idemodthingy.programs.bluej;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextField;

import me.itay.idemodthingy.api.IDELanguageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class SelectLanguageDialog extends Dialog{
    private static final int DIVIDE_WIDTH = 15;

    private String labelText = "";
    private String positiveText = "Okay";
    private String negativeText = "Cancel";

    private ResponseHandler<String> responseListener;

    private Button buttonPositive;
    private Button buttonNegative;
    private ComboBox.List<String> languages;

    public SelectLanguageDialog(String labelText) {
        this.labelText = labelText;
    }

    @Override
    public void init()
    {
        super.init();

        int offset = 0;



        defaultLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Gui.drawRect(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
        });

        languages = new ComboBox.List<>(5, 5 + offset, IDELanguageManager.getSupport().keySet().toArray(new String[IDELanguageManager.getSupport().keySet().size()]));
        addComponent(languages);

        int positiveWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(positiveText);
        buttonPositive = new Button(getWidth() - positiveWidth - DIVIDE_WIDTH, getHeight() - 20, positiveText);
        buttonPositive.setSize(positiveWidth + 10, 16);
        buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if(!languages.getSelectedItem().isEmpty())
            {
                boolean close = true;
                if(languages != null)
                {
                    close = responseListener.onResponse(true, languages.getSelectedItem().trim());
                }
                if(close) close();
            }
        });
        this.addComponent(buttonPositive);

        int negativeWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(negativeText);
        buttonNegative = new Button(getWidth() - DIVIDE_WIDTH - positiveWidth - DIVIDE_WIDTH - negativeWidth + 1, getHeight() - 20, negativeText);
        buttonNegative.setSize(negativeWidth + 10, 16);
        buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
        this.addComponent(buttonNegative);
    }

    /**
     * Sets the positive button text
     * @param positiveText
     */
    public void setPositiveText(@Nonnull String positiveText)
    {
        this.positiveText = positiveText;
    }

    /**
     * Sets the negative button text
     *
     * @param negativeText
     */
    public void setNegativeText(@Nonnull String negativeText)
    {
        this.negativeText = negativeText;
    }

    /**
     * Sets the response handler. The handler is called when the positive
     * button is pressed and returns the value in the input text field. Returning
     * true in the handler indicates that the dialog should close.
     *
     * @param responseListener
     */
    public void setResponseHandler(ResponseHandler<String> responseListener)
    {
        this.responseListener = responseListener;
    }
}