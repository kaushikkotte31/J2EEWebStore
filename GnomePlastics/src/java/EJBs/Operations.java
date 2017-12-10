/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJBs;

import Entities.Orders;
import Entities.Products;
import Entities.Users;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author bkotte
 */
@Stateful
public class Operations {

    @PersistenceContext(unitName = "GnomePlasticsPU")
    private EntityManager em;

    public int registerUser(String username, String password) {
        int result = checkUser(username, password, false);
        if (result == 0) {
            return 0; //User already exists
        }
        Users user = new Users(username, password, false, false);
        em.persist(user);
        return -1;
    }

    public int checkUser(String username, String password, boolean isLogin) {
        Users user = em.find(Users.class, username);
        if (user == null) {
            System.out.println("bhanu user not found");
            return 1; //No user
        }
        if (isLogin) {

            if (user.getIsBanned()) {
                return 3; //User banned
            }
            if (!user.getPassword().equals(password)) {
                return 2; // Password Mismatch
            }
        }

        return 0; //Login successful
    }

    public void persist(Object object) {
        em.persist(object);
    }

    public List<String> getCategories() {
        List<String> results = em.createQuery("SELECT DISTINCT c.category FROM Products c where c.stock > 0").getResultList();
        System.out.println("bhanu categories: " + results.toString());
        return results;
    }

    public List<Products> getProducts(String selectedCategory) {
        Query qry;
        if (!selectedCategory.equals("AdminAll")) {
            qry = em.createQuery("SELECT c FROM Products c where c.category= :category and c.stock>0");
            qry.setParameter("category", selectedCategory);
        } else {
            qry = em.createQuery("SELECT c FROM Products c ORDER BY c.category");
        }
        List<Products> results = qry.getResultList();
        return results;
    }

    public String cartCheckout(String username, HashMap<Products, Integer> cart, double total) {
        try {
            for (Map.Entry<Products, Integer> entry : cart.entrySet()) {
                System.out.println(entry.getKey() + "/" + entry.getValue());
                Products product = em.find(Products.class, entry.getKey().getName());
                product.setStock(product.getStock() - cart.get(product));
                em.persist(product);
            }
            byte[] cartData = SerializationUtils.serialize(cart);
            Orders newOrd = new Orders(username, cartData, total);
            em.persist(newOrd);
            String oid = "" + em.createQuery("SELECT max(o.id) FROM Orders o where o.username=:username").setParameter("username", username).getSingleResult();
            return oid;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public List<Orders> getOrders(String username) {
        return em.createQuery("SELECT o FROM Orders o where o.username=:username ORDER BY o.id DESC").setParameter("username", username).getResultList();
    }

    public List<Users> getAllUsers() {
        return em.createQuery("SELECT u FROM Users u where u.username<>'admin'ORDER BY u.username ASC").getResultList();
    }

    public void setBanStatus(String username) {
        Users user = em.find(Users.class, username);
        user.setIsBanned(!user.getIsBanned());
        em.persist(user);
    }

    public void removeProduct(String name) {
        Products product = em.find(Products.class, name);
        em.remove(product);
    }

    public boolean addProduct(String name, String category, double cost, int stock, boolean isAdd) {
        Products product=null;
        product = em.find(Products.class, name);
        if(isAdd && product !=null)
        {
            return false; //Another product with same name found
        }
        if (!isAdd) {
            
            if(product==null)
                return false; //No product with this name found
            if (!category.equals("")) {
                product.setCategory(category);
            }
            if (cost != 0) {
                product.setCost(cost);
            }
            if (stock != 0) {
                product.setStock(stock);
            }
        } else {
            product = new Products(name, category, cost, stock);
        }
        em.persist(product);
        return true;
    }
}
