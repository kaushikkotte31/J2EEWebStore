/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import EJBs.Operations;
import Entities.Products;
import Entities.Users;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author bhanu
 */
@ManagedBean
@SessionScoped
public class admin {

    /**
     * Creates a new instance of admin
     */
    @EJB
    private Operations op;
    List<Products> prodList;
    List<Users> usersList;
    String info;
    String prodName,prodCategory,prodStock,prodCost;
    
    public List<Products> getProdList() {
        prodList=op.getProducts("AdminAll");
        return prodList;
    }

    public void setProdList(List<Products> prodList) {
        this.prodList = prodList;
    }

    public List<Users> getUsersList() {
        usersList=op.getAllUsers();
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdCategory() {
        return prodCategory;
    }

    public void setProdCategory(String prodCategory) {
        this.prodCategory = prodCategory;
    }

    public String getProdCost() {
        return prodCost;
    }

    public void setProdCost(String prodCost) {
        this.prodCost = prodCost;
    }

    public String getProdStock() {
        return prodStock;
    }

    public void setProdStock(String prodStock) {
        this.prodStock = prodStock;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    
    public admin() {
    }

    public void banUser(String username)
    {
        op.setBanStatus(username);
                
    }
    
    public void deleteProduct(String name)
    {
        op.removeProduct(name);
    }
    
    public void updateProduct()
    {
        
        if(prodName.equals(""))
        {
            info="** Product name cannot be empty !";
            return;
        }
        
        if(prodCategory.equals("") && prodCost.equals("") && prodStock.equals(""))
        {
            info="** Either of category/cost/stock should be filled in !";
            return;
        }
        
        double cost;
        try
        {
            cost=Double.parseDouble(prodCost);
        }
        catch(NumberFormatException ex)
        {
             info="** Cost should be numerical value !";
            return;
        }
        
        int stock;
        try
        {
            stock=Integer.parseInt(prodStock);
        }
        catch(NumberFormatException ex)
        {
             info="** Stock should be integer value !";
            return;
        }
        
        if(op.addProduct(prodName,prodCategory,cost,stock,false))
        {
            info="** Successfully updated !";
            reset();
        }
        else
            info="** No product found !";
    }
    
     public void addProduct()
    {
        
        if(prodName.equals(""))
        {
            info="** Product name cannot be empty !";
            return;
        }
        
        if(prodCategory.equals(""))
        {
            info="** Product category cannot be empty !";
            return;
        }
        
        if(prodCost.equals(""))
        {
            info="** Product cost cannot be empty !";
            return;
        }
        
        if(prodStock.equals(""))
        {
            info="** Product stock cannot be empty !";
            return;
        }
        
        double cost;
        try
        {
            cost=Double.parseDouble(prodCost);
        }
        catch(NumberFormatException ex)
        {
             info="** Cost should be numerical value !";
            return;
        }
        
        int stock;
        try
        {
            stock=Integer.parseInt(prodStock);
        }
        catch(NumberFormatException ex)
        {
             info="** Stock should be integer value !";
            return;
        }
        
        if(op.addProduct(prodName,prodCategory,cost,stock,true))
        {
            info="** Successfully added !";
            reset();
        }
        else
            info="** Product with same name exists. Try updating else change name !";
    }
     
     void reset()
     {
         prodName="";
         prodCost="";
         prodCategory="";
         prodStock="";
         
     }
}
