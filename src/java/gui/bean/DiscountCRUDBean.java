/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.bean;

/**
 *
 * @author JuanCamilo
 */
import businessLogic.controller.DiscountCRUD;
import dataSourceManagement.DAO.DiscountDAO;
import dataSourceManagement.DAO.VehicleDAO;
import dataSourceManagement.entities.Discount;
import dataSourceManagement.entities.ShopOrder;
import dataSourceManagement.entities.Vehicle;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

@ManagedBean
@ViewScoped
public class DiscountCRUDBean {

    //Generated by Map
    private Map<String, Integer> availableVehicles;

    //Generated by Map
    private Map<String, Integer> availableDiscounts;

    private Integer selectedVehicleId;
    private Integer selectedDiscountId;

    private String expirationDate;
    private String description;
    private Float percentage;
    private ShopOrder shopOrderOrderId;
    private Vehicle vehicleId;
    private SimpleDateFormat sdf, sdf2;

    public DiscountCRUDBean() {
        selectedVehicleId = -1;
        selectedDiscountId = -1;
        sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",
                Locale.US);
        sdf2 = new SimpleDateFormat("dd/mm/yyyy");
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public ShopOrder getShopOrderOrderId() {
        return shopOrderOrderId;
    }

    public void setShopOrderOrderId(ShopOrder shopOrderOrderId) {
        this.shopOrderOrderId = shopOrderOrderId;
    }

    public Vehicle getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Vehicle vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getSelectedDiscountId() {
        return selectedDiscountId;
    }

    public void setSelectedDiscountId(Integer selectedDiscountId) {
        this.selectedDiscountId = selectedDiscountId;
    }

    public Map<String, Integer> getAvailableVehicles() {
        availableVehicles = new LinkedHashMap<>();
        VehicleDAO vdao = new VehicleDAO();
        List<Vehicle> vehicles = vdao.findVehicleEntities();
        for (Vehicle v : vehicles) {
            availableVehicles.put(v.getLabel(), v.getVehicleId());
        }
        return availableVehicles;
    }

    public Map<String, Integer> getAvailableDiscounts() {
        availableDiscounts = new LinkedHashMap<>();
        DiscountCRUD discountCRUD = new DiscountCRUD();
        List<Discount> discounts = discountCRUD.findDiscountEntities();
        for (Discount d : discounts) {
            availableDiscounts.put(d.getLabel(), d.getDiscountId());
        }
        return availableDiscounts;
    }

    public Integer getSelectedVehicleId() {
        return selectedVehicleId;
    }

    public void setSelectedVehicleId(Integer selectedVehicleId) {
        this.selectedVehicleId = selectedVehicleId;
    }

    public void createDiscount() {
        DiscountCRUD discountCRUD = new DiscountCRUD();
        try {
            Discount discount = new Discount();
            discount.setDescription(getDescription());
            System.out.println("Discount date: " + expirationDate);
            discount.setExpirationDate(sdf.parse(expirationDate));
            discount.setPercentage(getPercentage());
            Vehicle selectedVehicle = getSelectedVehicle();
            if (selectedVehicle == null) {
                System.out.println("any selected vehicle");
                return;
            }
            discount.setDiscountAmount(selectedVehicle.getSellPrice() * getPercentage());
            discount.setVehicleId(selectedVehicle);
            discountCRUD.createDiscount(discount);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void editDiscount() {
        DiscountCRUD discountCRUD = new DiscountCRUD();
        if (selectedVehicleId == -1) {
            System.err.println("any selected vehicle");
            return;
        }
        try {
            Discount edited = getSelectedDiscount();
            String key = edited.getLabel();
            edited.setDescription(getDescription());
            edited.setExpirationDate(sdf.parse(expirationDate));
            edited.setPercentage(getPercentage());
            Vehicle selectedVehicle = getSelectedVehicle();
            if (selectedVehicle == null) {
                System.out.println("any selected vehicle");
                return;
            }
            edited.setVehicleId(selectedVehicle);
            edited.setDiscountAmount(selectedVehicle.getSellPrice() * getPercentage());
            discountCRUD.editDiscount(edited);
            availableDiscounts.remove(key);
            availableDiscounts.put(edited.getLabel(), edited.getDiscountId());
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void deleteDiscount() {
        DiscountCRUD deleteCRUD = new DiscountCRUD();
        if (selectedDiscountId == -1) {
            System.err.println("any selected discount");
            return;
        }
        Discount deletedDiscount = getSelectedDiscount();
        String key = deletedDiscount.getLabel();
        deleteCRUD.deleteDiscount(getSelectedDiscountId());
        availableDiscounts.remove(key);
    }

    private Vehicle getSelectedVehicle() {
        VehicleDAO vdao = new VehicleDAO();
        return vdao.findVehicle(getSelectedVehicleId());
    }

    @Override
    public String toString() {
        return "Selected v " + selectedVehicleId
                + " description " + description;
    }

    private Discount getSelectedDiscount() {
        DiscountDAO vdao = new DiscountDAO();
        return vdao.findDiscount(getSelectedDiscountId());
    }

    public void fillDiscountData(ValueChangeEvent e) {
        setSelectedDiscountId(Integer.parseInt(e.getNewValue().toString()));
        Discount selected = getSelectedDiscount();
        System.out.println("discount to fill " + selected.getLabel());
        this.setDescription(selected.getDescription());
        this.setExpirationDate(sdf2.format(selected.getExpirationDate()));
        this.setPercentage(selected.getPercentage());
        this.setSelectedVehicleId(selected.getVehicleId().getVehicleId());
        FacesContext.getCurrentInstance().renderResponse();
    }

}
