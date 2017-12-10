/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import EJBs.Operations;
import Entities.Orders;
import Entities.Products;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
//import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
//mport org.primefaces.context.RequestContext;

@ManagedBean(name = "login")
@SessionScoped
public class login {

    /**
     * Creates a new instance of login
     */
    @EJB
    private Operations op;
    String username, password, confpassword, signupname, selectedCategory, errInfo, orderId;
    HashMap<Products, Integer> cart = new HashMap<Products, Integer>();
    private static boolean isLoggedIn;
    Boolean error;
    List<String> category;
    List<Products> prodList;
    List<Orders> orderList;
    String errMsg;
    static double cartTotal;

    public List<Orders> getOrderList() {
        orderList = op.getOrders(username);
        return orderList;
    }

    public void setOrderList(List<Orders> orderList) {
        this.orderList = orderList;
    }

    public HashMap<Products, Integer> getCart() {
        return cart;
    }

    public void setCart(HashMap<Products, Integer> cart) {
        this.cart = cart;
    }

    public double getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(double cartTotal) {
        this.cartTotal = cartTotal;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    public List<Products> getProdList() {
        return prodList;
    }

    public void setProdList(List<Products> prodList) {
        this.prodList = prodList;
    }

    public String getSignupname() {
        return signupname;
    }

    public void setSignupname(String signupname) {
        this.signupname = signupname;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getConfpassword() {
        return confpassword;
    }

    public void setConfpassword(String confpassword) {
        this.confpassword = confpassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getCategory() {
        // errMsg = "";
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public boolean isIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLogged) {
        isLoggedIn = isLogged;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public login() {
        username = "";
        password = "";
        confpassword = "";
        signupname = "";
        selectedCategory = "";
        isLoggedIn = false;
        cartTotal = 0;
    }

    public void loginCheck() {
        if (isLoggedIn) {
            try {
                if (username.equals("admin")) {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("Admin.xhtml");
                } else {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("Home.xhtml");
                }
            } catch (IOException ex) {
                Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String loginUser() {
        if (username.equals("") || password.equals("")) {
            error = true;
            errMsg = "\t\t\t** Username or password cannot be empty";
            return "";
        }
        int result = op.checkUser(username, password, true);
        String retVal = "";
        switch (result) {
            case 0: //Login success
                error = false;
                errMsg = "";
                category = op.getCategories();
                if (category.size() > 0) {
                    if (selectedCategory.equals("")) {
                        selectedCategory = category.get(0);
                    }
                    getProducts(selectedCategory);

                }
                isLoggedIn = true;
                if (username.equals("admin")) {
                    retVal = "Admin";
                } else {
                    retVal = "Home";
                }
                break;

            case 1: //No user
                error = true;
                errMsg = "\t\t\t** Invalid Username";
                break;

            case 2: //Password mismatch
                error = true;
                errMsg = "\t\t\t** Password mismatch";
                break;

            case 3: //User banned
                error = true;
                errMsg = "\t\t\t** User banned by admin";
                break;

        }
        return retVal;
    }

    public String registerUser() {
        System.out.println("bhanu in register");
        if (signupname.equals("") || password.equals("") || confpassword.equals("")) {
            error = true;
            errMsg = "\t\t\t** Username or password/confirm password cannot be empty";
            return "";
        }
        if (!password.equals(confpassword)) {
            error = true;
            errMsg = "\t\t\t** password and confirm password mismatch";
            return "";
        }
        int result = op.registerUser(signupname, password);
        System.out.println("result= " + result);
        if (result == 0) {
            error = true;
            errMsg = "\t\t\t** Username not available please try another !";
            return "";
        } else {
            error = false;
            errMsg = "";
            category = op.getCategories();
            if (category.size() > 0) {
                if (selectedCategory.equals("")) {
                    selectedCategory = category.get(0);
                }
                getProducts(selectedCategory);

            }
            isLoggedIn = true;
            return "Home";
            //redirect to new;
        }
    }

    public String logout() {
        ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).invalidate();
        return "Login";
    }

    public void getProducts(String selected) {
        errMsg = "";
        selectedCategory = selected;
        prodList = op.getProducts(selectedCategory);
    }

    public void addToCart(Products p) {
        errMsg = "** " + p.getName() + " Added to cart successfully !! **";
        errInfo = "";

        if (cart.containsKey(p)) {
            int avbl = p.getStock();
            int inCart = Integer.parseInt(cart.get(p) + "");
            System.out.println(cart.get(p));
            if (inCart == avbl) {
                errMsg = "** Stock exceeded !! **";
                errInfo = "** Stock exceeded !! **";
                return;
            }
            cartTotal += p.getCost();
            cart.put(p, inCart + 1);
            return;
        }
        cartTotal += p.getCost();
        cart.put(p, 1);
    }

    public void deleteFromCart(Products p) {
        errInfo = "";
        int inCart = Integer.parseInt(cart.get(p) + "");
        cartTotal -= inCart * p.getCost();
        cart.remove(p);
    }

    public void reduceInCart(Products p) {
        errInfo = "";
        if (cart.containsKey(p)) {
            int avbl = p.getStock();
            int inCart = Integer.parseInt(cart.get(p) + "");

            if (inCart == 1) {
                errInfo = "** Minimun quantity is 1 !! **";
                return;
            }
            cartTotal -= p.getCost();
            cart.put(p, inCart - 1);
        }
    }

    public int getQuantity(Products p) {
        if (cart.containsKey(p)) {
            return cart.get(p);
        }
        return 0;
    }

    public String checkout() {
        orderId = op.cartCheckout(username, cart, cartTotal);
        if (orderId.equals("")) {
            errInfo = "** Order was not placed. Try again !! **";
            return "";
        }
        cart.clear();
        return "History";
    }

}
