package com.example.demo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
//import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
//import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.component.Component;

@Route("id")
public class MainView extends VerticalLayout {

    private PersonRepository repository;
    private TextField userName = new TextField( "Username");
    private TextField password = new TextField("Password");
    //private EmailField email = new EmailField("Email");
    private Grid<Person> grid = new Grid<>(Person.class);
    private Binder<Person> binder = new Binder<>(Person.class);


    public MainView(PersonRepository repository){
        this.repository = repository;
        grid.setColumns("userName", "password");
        add(getForm(), grid);
        refreshGrid();
    }
    private Component getForm(){
        var layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);

        var addButton = new Button("Add");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(userName, password, addButton);

        binder.bindInstanceFields(this);

        addButton.addClickListener(click -> {
            try{
                var person = new Person();
                binder.writeBean(person);
                repository.save(person);
                refreshGrid();
            }catch(ValidationException e) {
                //
            }
        });

        return layout;
    }
    private void refreshGrid(){
        grid.setItems(repository.findAll());
    }
}
