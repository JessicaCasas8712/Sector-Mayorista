/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataSourceManagement.DAO;

import dataSourceManagement.entities.Order;
import dataSourceManagement.entities.Payment;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author afacunaa
 */
public class OrderDAO {
    
    public EntityManagerFactory emf1 = Persistence.createEntityManagerFactory("MayoristaPU");
    
    public Order persist(Order order) {
        EntityManager em = emf1.createEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(order);
            em.getTransaction().commit();
        } catch(Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return order;
    }
    
    public void buyAutos(Order order){
        PaymentDAO paymentDAO = new PaymentDAO();
        
        Integer paymentId = paymentDAO.newPaymentId();
        String type="Efectivo";
        Date date = new Date();
        String debt ="";
        
        Payment payment = new Payment();
        payment.setDate(date);
        payment.setDebt(debt);
        payment.setType(type);
        payment.setPaymentId(paymentId);
        payment.setOrderOrderId(order);
        
        paymentDAO.persist(payment);
        
        order.setState("Finalizada");
    }
    
    public Order searchByOrderId(Integer orderId){
        
        EntityManager em = emf1.createEntityManager();
        Order order = null;
        Query q = em.createNamedQuery("Order.findByOrderId");
        q.setParameter(1, orderId);
        try {
            order = (Order) q.getSingleResult();
        } catch (Exception e) {
        } finally {
            em.close();
        }
        return order;
    }
    
    public Integer newOrderId(){
        Integer id = 0;
        EntityManager em = emf1.createEntityManager();
        Order order = null;
        Query q = em.createNamedQuery("Order.findAll");
        try {
            id = q.getResultList().size();
        } catch (Exception e) {
        } finally {
            em.close();
        }
        return id;
    }
    
}