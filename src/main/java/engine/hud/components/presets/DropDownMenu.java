package engine.hud.components.presets;

import engine.general.MouseInput;
import engine.hud.actions.Action;
import engine.hud.actions.MouseAction;
import engine.hud.actions.ReturnAction;
import engine.hud.color.Color;
import engine.hud.components.contentcomponents.QuadComponent;
import engine.hud.components.contentcomponents.TextComponent;
import engine.hud.components.layout.ExpandList;
import engine.hud.constraints.positionConstraints.RelativeInParent;
import engine.hud.constraints.sizeConstraints.AbsoluteAspectRatio;
import engine.hud.constraints.sizeConstraints.RelativeToParentSize;
import engine.hud.constraints.sizeConstraints.RelativeToScreenSize;
import engine.hud.constraints.sizeConstraints.TextAspectRatio;
import engine.hud.text.FontTexture;

import java.util.ArrayList;
import java.util.List;

public class DropDownMenu<T> extends QuadComponent {

    private final int maxHeight = 5;

    private int height;

    private ExpandList contentBack;

    private List<DropDownElement<? extends T>> content;

    private TextComponent currentlySelected;

    private VerticalScrollView scrollView;

    private int elementNumber;

    private Color backGroundColor,textColor,effectColor,effectTextColor,iconColor;

    private Action onChangedAction;

    public DropDownMenu() {
        backGroundColor = Color.WHITE;
        textColor = Color.BLACK;
        effectColor = Color.BLUE;
        effectTextColor = Color.WHITE;
        iconColor = Color.GREY;
        build();
    }

    public DropDownMenu(Color backGroundColor, Color textColor, Color effectColor, Color effectTextColor, Color iconColor) {
        this.backGroundColor = backGroundColor;
        this.textColor = textColor;
        this.effectColor = effectColor;
        this.effectTextColor = effectTextColor;
        this.iconColor = iconColor;

        build();
    }

    protected void build() {

        content = new ArrayList<>();

        this.setWidthConstraint(new RelativeToScreenSize(0.1f));
        this.setHeightConstraint(new AbsoluteAspectRatio(0.15f));
        this.setColors(backGroundColor);

        currentlySelected = new TextComponent(FontTexture.STANDARD_FONT_TEXTURE);
        currentlySelected.setHeightConstraint(new RelativeToParentSize(0.8f));
        currentlySelected.setWidthConstraint(new TextAspectRatio());
        currentlySelected.setxPositionConstraint(new RelativeInParent(0.1f));
        currentlySelected.setyPositionConstraint(new RelativeInParent(0.5f));
        currentlySelected.setColors(textColor);

        this.addComponent(currentlySelected);

        QuadComponent icon = new QuadComponent();
        icon.setHeightConstraint(new RelativeToParentSize(1));
        icon.setWidthConstraint(new AbsoluteAspectRatio(1));
        icon.setxPositionConstraint(new RelativeInParent(1));
        icon.setyPositionConstraint(new RelativeInParent(0.5f));
        icon.setColors(iconColor);
        icon.setSelectable(false);

        this.addComponent(icon);

        contentBack = new ExpandList(ExpandList.Orientation.HORIZONTAL);
        contentBack.setHeightConstraint(new RelativeToParentSize(0));
        contentBack.setElementsPerPage(5);

        scrollView = new VerticalScrollView();

        scrollView.setWidthConstraint(new RelativeToParentSize(1));
        scrollView.setHeightConstraint(new RelativeToParentSize(0));
        scrollView.setyPositionConstraint(new RelativeInParent(0));
        scrollView.setxPositionConstraint(new RelativeInParent(0.5f));

        scrollView.setUseMask(false);

        scrollView.setVisible(false);

        scrollView.setContent(contentBack);

        this.addComponent(scrollView);

        this.setOnLeftClickAction(() -> {

            if(getSceneComponent().getRenderedOnTop() != null && getSceneComponent().getRenderedOnTop().equals(scrollView)) {
                getSceneComponent().setRenderedOnTop(null);
            } else {
                getSceneComponent().setRenderedOnTop(scrollView);
            }
            hud.needsNextRendering();
            return true;
        });

        this.setDeselectedAction(() -> {

            getSceneComponent().setRenderedOnTop(null);
            hud.needsNextRendering();
        });

    }

    public void setSelected(String name) {
        if(!name.equals(currentlySelected.getText())) {
            if(getDropDownElement(currentlySelected.getText()) != null) {
                getDropDownElement(currentlySelected.getText()).deselect();
            }
            currentlySelected.setText(name);
            getDropDownElement(name).select();
            if(onChangedAction != null) {
                onChangedAction.execute();
            }
        }
    }

    public void setSelectedWithoutAction(String name) {
        if(getDropDownElement(currentlySelected.getText()) != null) {
            getDropDownElement(currentlySelected.getText()).deselect();
        }
        currentlySelected.setText(name);
        getDropDownElement(name).select();
    }

    public void addElement(String name,T element) {
        DropDownElement<T> element1 = new DropDownElement<>(name,this,element);
        content.add(element1);
        contentBack.addComponent(element1);
        height++;
        if(elementNumber == 0) {
            setSelected(name);
        }
        elementNumber++;
        if(height <= maxHeight) {
            contentBack.changeHeightValue(1);
            scrollView.setyOffset(1.0f/height);
            scrollView.changeHeightValue(height);
        } else {
            contentBack.changeHeightValue(height * (1.0f/maxHeight));
            scrollView.changeHeightValue(maxHeight);
        }
        scrollView.calculateValues();
    }

    public void removeElement(String name) {
        DropDownElement element = null;
        for(DropDownElement element1 : content ) {
            if(element1.getText().equals(name)) {
                element = element1;
            }
        }
        if(element != null) {
            contentBack.removeComponent(element);
            height--;
            elementNumber--;

            if(currentlySelected.getText().equals(name)) {
                if(elementNumber > 0) {
                    currentlySelected.setText(content.get(0).getText());
                }
            }

            if(height <= maxHeight) {
                scrollView.setyOffset(1.0f/height);
                scrollView.changeHeightValue(height);
            } else {
                scrollView.changeHeightValue(maxHeight);
            }
            scrollView.calculateValues();
        }
    }

    public T getElement(String name) {
        for(DropDownElement<? extends T> element : content) {
            if(element.getText().equals(name)) {
                return (T) element.getElement();
            }
        }
        return null;
    }

    public DropDownElement getDropDownElement(String name) {
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

    public VerticalScrollView getScrollView() {
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

    private static class DropDownElement<T> extends QuadComponent {

        private String text;

        private T element;

        private TextComponent textComponent;

        private DropDownMenu dropDownMenu;

        private boolean selected;

        public DropDownElement(String text, DropDownMenu dropDownMenu, T element) {

            this.element = element;

            this.text = text;

            this.dropDownMenu = dropDownMenu;

            this.setWidthConstraint(new RelativeToParentSize(1));
            this.setxPositionConstraint(new RelativeInParent(0));

            this.setColors(dropDownMenu.getBackGroundColor());


            textComponent = new TextComponent(FontTexture.STANDARD_FONT_TEXTURE);
            textComponent.setText(text);
            textComponent.setHeightConstraint(new RelativeToParentSize(0.8f));
            textComponent.setWidthConstraint(new TextAspectRatio());
            textComponent.setyPositionConstraint(new RelativeInParent(0.5f));
            textComponent.setxPositionConstraint(new RelativeInParent(0.1f));

            textComponent.setColors(dropDownMenu.getTextColor());

            this.addComponent(textComponent);

            this.setOnLeftClickAction(new ReturnAction() {
                @Override
                public boolean execute() {
                    dropDownMenu.setSelected(text);
                    hud.needsNextRendering();
                    return true;
                }
            });

            this.setMouseEnteredAction(new MouseAction() {
                @Override
                public boolean action(MouseInput mouseInput) {
                    DropDownElement.this.setColors(dropDownMenu.getEffectColor());
                    textComponent.setColors(dropDownMenu.getEffectTextColor());
                    hud.needsNextRendering();
                    return true;
                }
            });

            this.setMouseExitedAction(new MouseAction() {
                @Override
                public boolean action(MouseInput mouseInput) {
                    if(!selected) {
                        DropDownElement.this.setColors(dropDownMenu.getBackGroundColor());
                        textComponent.setColors(dropDownMenu.getTextColor());
                        hud.needsNextRendering();
                    }
                    return true;
                }
            });
        }

        public String getText() {
            return text;
        }

        public T getElement() {
            return element;
        }

        public void deselect() {
            selected = false;
            DropDownElement.this.setColors(dropDownMenu.getBackGroundColor());
            textComponent.setColors(dropDownMenu.getTextColor());
        }

        public void select() {
            selected = true;
            DropDownElement.this.setColors(dropDownMenu.getEffectColor());
            textComponent.setColors(dropDownMenu.getEffectTextColor());
        }
    }
}
