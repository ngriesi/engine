package engine.hud.components.presets;

import engine.hud.actions.Action;
import engine.hud.color.Color;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.components.layout.ExpandList;
import engine.hud.components.presets.scroll.ScrollView;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.AbsoluteAspectRatio;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToScreenSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.mouse.MouseEvent;
import engine.hud.text.FontTexture;

import java.util.ArrayList;
import java.util.List;

import static engine.hud.mouse.MouseListener.MouseButton.LEFT;

/**
 * class used to add simple drop down menu to the hud with string contents
 * each element has a name which is displayed on the menu linked with an object
 * of the generic type T
 *
 * @param <T> generic typ of which each element saves one object
 */
@SuppressWarnings("unused")
public class DropDownMenu<T> extends QuadComponent {

    /**
     * defines how many lines are displayed in the drop down menu without scrolling
     *
     * 5 means 5 lines plus the initial unexpanded field
     */
    private final int maxHeight = 5;

    /**
     * attribute saving the actual height of the menu in lines
     * (can vary from maxHeight if the number of elements is smaller
     * than maxHeight)
     */
    private int height;

    /**
     * layout component which contains the elements of the dropdown menu
     */
    private ExpandList contentBack;

    /**
     * list of dropDownElements - this saves the actual content of the drop down menu
     */
    private List<DropDownElement<? extends T>> content;

    /**
     * text component which is always visible and displays the current selection of the
     * menu
     */
    private TextComponent currentlySelected;

    /**
     * scroll view containing the contentBack object and with it all the lines of the
     * menu
     */
    private ScrollView scrollView;

    /**
     * number of elements which are currently in the drop down menu
     */
    private int elementNumber;

    /**
     * colors of the drop down menu for creating a general layout
     */
    private Color backGroundColor,textColor,effectColor,effectTextColor,iconColor;

    /**
     * action performed when the currently selected element is changed
     *
     * to change the current selection without this action being triggered
     * use the <code>setSelectedWithoutAction</code> method
     */
    private Action onChangedAction;

    /**
     * default constructor using the standard colors
     */
    public DropDownMenu() {
        this(Color.LIGHT_GRAY,Color.BLACK,Color.BLUE,Color.WHITE,Color.GREY);
        build();
    }

    /**
     * constructor setting all the colors
     *
     * @param backGroundColor color of the background of the whole drop down menu
     * @param textColor color of all the text in the component in its standard form
     * @param effectColor color of the highlighted lines in the menu
     * @param effectTextColor color of the text in the highlighted lines of the menu
     * @param iconColor color of the icon in the top right of the menu
     */
    public DropDownMenu(Color backGroundColor, Color textColor, Color effectColor, Color effectTextColor, Color iconColor) {
        this.backGroundColor = backGroundColor;
        this.textColor = textColor;
        this.effectColor = effectColor;
        this.effectTextColor = effectTextColor;
        this.iconColor = iconColor;
        build();
    }

    /**
     * crates all the necessary components
     */
    protected void build() {

        content = new ArrayList<>();

        // sets the size of the menu when it is collapsed
        this.setWidthConstraint(new RelativeToScreenSize(0.1f));
        this.setHeightConstraint(new AbsoluteAspectRatio(0.15f));
        this.setColors(backGroundColor);

        // formats the main text of the menu
        currentlySelected = new TextComponent(FontTexture.STANDARD_FONT_TEXTURE);
        currentlySelected.setHeightConstraint(new RelativeToParentSize(0.8f));
        currentlySelected.setWidthConstraint(new TextAspectRatio());
        currentlySelected.setXPositionConstraint(new RelativeInParent(0.1f));
        currentlySelected.setYPositionConstraint(new RelativeInParent(0.5f));
        currentlySelected.setColors(textColor);

        this.addComponent(currentlySelected);

        // formats the icon of the menu
        QuadComponent icon = new QuadComponent();
        icon.setHeightConstraint(new RelativeToParentSize(1));
        icon.setWidthConstraint(new AbsoluteAspectRatio(1));
        icon.setXPositionConstraint(new RelativeInParent(1));
        icon.setYPositionConstraint(new RelativeInParent(0.5f));
        icon.setColors(iconColor);
        icon.setFocusable(false);

        this.addComponent(icon);

        // formats the back of the element list
        contentBack = new ExpandList(ExpandList.Orientation.HORIZONTAL);
        contentBack.setHeightConstraint(new RelativeToParentSize(0));
        contentBack.setElementsPerPage(5);
        contentBack.setWidthConstraint(1);

        // formats the scroll view
        scrollView = new ScrollView();
        scrollView.setWidthConstraint(new RelativeToParentSize(1));
        scrollView.setHeightConstraint(new RelativeToParentSize(0));
        scrollView.setYPositionConstraint(new RelativeInParent(0));
        scrollView.setXPositionConstraint(new RelativeInParent(0.5f));

        scrollView.setVisible(false);

        scrollView.setContent(contentBack);

        this.addComponent(scrollView);

        // sets the mouse behaviour
        this.getMouseListener().addMouseAction(e -> {
            if(e.getEvent() == MouseEvent.Event.DRAG_RELEASED && e.getMouseButton() == LEFT) {
                return true;
            }
            if(e.getEvent() == MouseEvent.Event.CLICK_RELEASED && e.getMouseButton() == LEFT) {

                getSceneComponent().setRenderedOnTop(scrollView);
                scrollView.setVisible(true);
                hud.needsNextRendering();
                return true;
            }
            return false;
        });

        // makes the menu collapse when it is deselected
        this.setOnFocusLostAction(() -> {

            getSceneComponent().setRenderedOnTop(null);
            hud.needsNextRendering();
        });

    }

    /**
     * sets the new selected item. This method is called when the user clicks
     * on one of the drop down elements
     *
     * @param name name of the newly selected item
     */
    public void setSelected(String name) {
        // only if something changes
        if(!name.equals(currentlySelected.getText())) {

            // deselects old item
            if(getDropDownElement(currentlySelected.getText()) != null) {
                getDropDownElement(currentlySelected.getText()).deselect();
            }

            // selects new item
            currentlySelected.setText(name);
            getDropDownElement(name).select();

            // calls the onChangedAction if it exists
            if(onChangedAction != null) {
                onChangedAction.execute();
            }
        }
    }

    /**
     * sets a new selection without calling the onChangedAction
     *
     * @param name of the new selection
     */
    public void setSelectedWithoutAction(String name) {
        //deselects old
        if(getDropDownElement(currentlySelected.getText()) != null) {
            getDropDownElement(currentlySelected.getText()).deselect();
        }

        //selects new
        currentlySelected.setText(name);
        getDropDownElement(name).select();
    }

    /**
     * adds a new line to the drop down menu with a name and a content element
     *
     * @param name of the new line (and its identifier)
     * @param element content of the new line
     */
    public void addElement(String name,T element) {

        DropDownElement<T> element1 = new DropDownElement<>(name,this,element);
        content.add(element1);
        contentBack.addComponent(element1);
        height++;

        // if its the first item added it gets selected
        if(elementNumber == 0) {
            setSelected(name);
        }
        elementNumber++;

        // sets new height of the drop down menu if necessary
        if(height <= maxHeight) {
            contentBack.changeHeightValue(1);
            scrollView.setYOffset(1.0f/height);
            scrollView.changeHeightValue(height);
        } else {
            contentBack.changeHeightValue(height * (1.0f/maxHeight));
            scrollView.changeHeightValue(maxHeight);
        }

        // updates the scroll view
        scrollView.calculateValues();
    }

    /**
     * removes an element form the drop down menu and selects a different
     * one if the removed one was currently selected
     *
     * @param name of the item to be removed
     */
    public void removeElement(String name) {

        // check if the element exists
        DropDownElement<? extends T> element = null;
        for(DropDownElement<? extends T> element1 : content ) {
            if(element1.getText().equals(name)) {
                element = element1;
            }
        }
        if(element != null) {
            contentBack.removeComponent(element);
            height--;
            elementNumber--;

            // selects the first element if the one that gets removed was selected
            if(currentlySelected.getText().equals(name)) {
                if(elementNumber > 0) {
                    currentlySelected.setText(content.get(0).getText());
                }
            }

            // changes the height of the drop down menu if necessary
            if(height <= maxHeight) {
                scrollView.setYOffset(1.0f/height);
                scrollView.changeHeightValue(height);
            } else {
                scrollView.changeHeightValue(maxHeight);
            }

            // updates the scroll view
            scrollView.calculateValues();
        }
    }

    /**
     * returns an element of the generic class by its corresponding name if
     * it exists in the drop down menu
     *
     * @param name of the element to be accessed
     * @return the corresponding element or null if it does not exist
     */
    public T getElement(String name) {
        for(DropDownElement<? extends T> element : content) {
            if(element.getText().equals(name)) {
                return element.getElement();
            }
        }
        return null;
    }

    /**
     * returns the drop down element object with the specified name if it exists
     * inside the drop down menu
     *
     * @param name of the element
     * @return the drop down element or null if it does not exist
     */
    public DropDownElement<? extends T> getDropDownElement(String name) {
        for(DropDownElement<? extends T> element : content) {
            if(element.getText().equals(name)) {
                return element;
            }
        }
        return null;
    }

    public T getSelectedElement() {
        return getElement(currentlySelected.getText());
    }

    public void setOnChangedAction(Action onChangedAction) {
        this.onChangedAction = onChangedAction;
    }

    public ExpandList getContentBack() {
        return contentBack;
    }

    public List<DropDownElement<? extends T>> getDropDownContent() {
        return content;
    }

    public TextComponent getCurrentlySelected() {
        return currentlySelected;
    }

    public ScrollView getScrollView() {
        return scrollView;
    }

    public Color getBackGroundColor() {
        return backGroundColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getEffectColor() {
        return effectColor;
    }

    public Color getEffectTextColor() {
        return effectTextColor;
    }

    public Color getIconColor() {
        return iconColor;
    }

    public void setBackGroundColor(Color backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
    }

    public void setEffectTextColor(Color effectTextColor) {
        this.effectTextColor = effectTextColor;
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
    }

    /**
     * class containing the information for individual lines of the drop down menu
     *
     * @param <T> generic type of the object saved in an element
     */
    private static class DropDownElement<T> extends QuadComponent {

        /**
         * name of the element - this is its identifier so there should not be duplicates
         */
        private String text;

        /**
         * object saved with this line
         */
        private T element;

        /**
         * text component for displaying the name of the line
         */
        private final TextComponent textComponent;

        /**
         * reference to the menu this item belongs to
         */
        private final DropDownMenu<? extends T> dropDownMenu;

        /**
         * saves if the this element is currently selected
         */
        private boolean selected;

        /**
         * constructor creating a new drop down menu line with name and object to be stored
         *
         * @param text name of the line and its identifier
         * @param dropDownMenu menu this element belongs to
         * @param element object saved with the line
         */
        public DropDownElement(String text, DropDownMenu<? extends T> dropDownMenu, T element) {

            this.element = element;

            this.text = text;

            this.dropDownMenu = dropDownMenu;

            // formats the lines background
            this.setWidthConstraint(new RelativeToParentSize(1));
            this.setXPositionConstraint(new RelativeInParent(0));
            this.setColors(dropDownMenu.getBackGroundColor());


            // formats the lines text
            textComponent = new TextComponent(FontTexture.STANDARD_FONT_TEXTURE);
            textComponent.setText(text);
            textComponent.setHeightConstraint(new RelativeToParentSize(0.8f));
            textComponent.setWidthConstraint(new TextAspectRatio());
            textComponent.setYPositionConstraint(new RelativeInParent(0.5f));
            textComponent.setXPositionConstraint(new RelativeInParent(0.1f));

            textComponent.setColors(dropDownMenu.getTextColor());

            this.addComponent(textComponent);

            // creates mouse behavior
            this.getMouseListener().addMouseAction(e -> {
                switch (e.getEvent()) {
                    case CLICK_RELEASED:
                        if(e.getMouseButton() == LEFT) {
                            dropDownMenu.setSelected(text);
                            hud.needsNextRendering();
                            return true;
                        }
                    case ENTERED:
                        DropDownElement.this.setColors(dropDownMenu.getEffectColor());
                        textComponent.setColors(dropDownMenu.getEffectTextColor());
                        hud.needsNextRendering();
                        return true;
                    case EXITED:
                        if(!selected) {
                            DropDownElement.this.setColors(dropDownMenu.getBackGroundColor());
                            textComponent.setColors(dropDownMenu.getTextColor());
                            hud.needsNextRendering();
                        }
                        return true;
                }
                return false;
            });
        }

        public String getText() {
            return text;
        }

        public T getElement() {
            return element;
        }

        /**
         * called when another item gets selected
         */
        public void deselect() {
            selected = false;
            DropDownElement.this.setColors(dropDownMenu.getBackGroundColor());
            textComponent.setColors(dropDownMenu.getTextColor());
        }

        /**
         * called when this item gets selected
         */
        public void select() {
            selected = true;
            DropDownElement.this.setColors(dropDownMenu.getEffectColor());
            textComponent.setColors(dropDownMenu.getEffectTextColor());
        }

        public void setText(String text) {
            this.text = text;
            textComponent.setText(text);
        }

        public void setElement(T element) {
            this.element = element;
        }
    }
}
