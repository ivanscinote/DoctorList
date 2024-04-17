package de.wetego.vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import de.wetego.vaadin.model.Doctor;
import de.wetego.vaadin.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
@Theme("demo")
@Title("Doctors Application")
@SpringUI
@PreserveOnRefresh
public class DoctorsUI extends UI {

    @Autowired
    private DoctorService doctorService;

    private Table doctorTable;
    private Label nameLabel;
    private Label specialtyLabel;

    @Override
    protected void init(VaadinRequest request) {
        final HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setMargin(true);
        leftLayout.setSpacing(true);

        final TextField nameField = new TextField();
        nameField.setCaption("Ime zdravnika:");
        nameField.setWidth(200, Unit.PIXELS);
        nameField.setInputPrompt("Vnesite ime zdravnika");

        final TextField specialtyField = new TextField();
        specialtyField.setCaption("Specializacija:");
        specialtyField.setWidth(200, Unit.PIXELS);
        specialtyField.setInputPrompt("Vnesite tip specializacije");

        Button addButton = new Button("Dodaj zdravnika");
        addButton.addClickListener(e -> {
            String doctorName = nameField.getValue();
            String doctorSpecialty = specialtyField.getValue();
            if (!doctorName.isEmpty() && !doctorSpecialty.isEmpty()) {
                Doctor newDoctor = doctorService.addDoctor(doctorName, doctorSpecialty);
                updateDoctorTable();
                nameLabel.setValue("");
                specialtyLabel.setValue("");
                Notification.show("Dodan zdravnik: " + doctorName + ", " + doctorSpecialty);
            } else {
                Notification.show("Prosim dodajte ime in specializacijo.", Notification.Type.WARNING_MESSAGE);
            }
        });

        leftLayout.addComponents(nameField, specialtyField, addButton);

        doctorTable = new Table("Seznam zdravnikov");
        doctorTable.addContainerProperty("Ime", String.class, null);
        doctorTable.addContainerProperty("Specializacija", String.class, null);
        doctorTable.addItemClickListener(event -> {
            Object itemId = event.getItemId();
            if (itemId instanceof Long) {
                Long doctorId = (Long) itemId;
                Doctor selectedDoctor = doctorService.findById(doctorId);
                if (selectedDoctor != null) {
                    nameLabel.setValue(selectedDoctor.getName());
                    specialtyLabel.setValue(selectedDoctor.getSpecialty());
                }
            }
        });
        updateDoctorTable();

        doctorTable.setCellStyleGenerator((Table.CellStyleGenerator) (source, itemId, propertyId) -> {
            if (itemId != null && propertyId == null) {
                return "pointer";
            }
            return null;
        });


        leftLayout.addComponent(doctorTable);

        mainLayout.addComponent(leftLayout);
        
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setMargin(true);
        rightLayout.setSpacing(true);

        nameLabel = new Label();
        specialtyLabel = new Label();

        rightLayout.addComponents(new Label("Podrobnosti zdravnika"), nameLabel, specialtyLabel);
        mainLayout.addComponent(rightLayout);

        setContent(mainLayout);
    }

    private void updateDoctorTable() {
        doctorTable.removeAllItems();
        for (Doctor doctor : doctorService.findAll()) {
            doctorTable.addItem(new Object[]{doctor.getName(), doctor.getSpecialty()}, doctor.getId());
        }
    }
    
    @WebServlet(urlPatterns = "/*", name = "DoctorsUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = DoctorsUI.class, productionMode = false)
    public static class DoctorsUIServlet extends SpringVaadinServlet {
    }
}
