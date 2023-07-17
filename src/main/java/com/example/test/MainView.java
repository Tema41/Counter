package com.example.test;

import com.example.test.model.Counter;
import com.example.test.repository.CounterRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("main")
public class MainView extends VerticalLayout {

    private final CounterRepository counterRepository;
    private final Binder<Counter> binder;
    private final NativeLabel valueLabel;
    private final TextField valueField;

    @Autowired
    public MainView(CounterRepository counterRepository) {
        this.counterRepository = counterRepository;

        valueLabel = new NativeLabel("0");
        valueField = new TextField();
        Button increaseButton = new Button("Increase", event -> increaseValue());

        binder = new Binder<>(Counter.class);
        binder.setBean(new Counter());

        binder.forField(valueField)
                .withConverter(new StringToIntegerConverter("Invalid number"))
                .bind(Counter::getValue, Counter::setValue);

        binder.addValueChangeListener(event -> {
            Counter counter = binder.getBean();
            valueLabel.setText(String.valueOf(counter.getValue()));
            saveValueToDatabase(counter.getValue());
        });

        VerticalLayout contentLayout = new VerticalLayout(valueLabel, valueField);
        contentLayout.setHorizontalComponentAlignment(Alignment.CENTER, valueLabel, valueField);

        add(contentLayout, increaseButton);
        setHorizontalComponentAlignment(Alignment.CENTER, contentLayout, increaseButton);

    }

    private void increaseValue() {
        int currentValue = Integer.parseInt(valueLabel.getText());
        currentValue++;
        valueLabel.setText(String.valueOf(currentValue));
        valueField.setValue(String.valueOf(currentValue));
        saveValueToDatabase(currentValue);
    }

    private void saveValueToDatabase(int value) {
        Counter counter = counterRepository.findById(1L).orElse(new Counter());
        counter.setId(1L);
        counter.setValue(value);
        counterRepository.save(counter);
    }
}
