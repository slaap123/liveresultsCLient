/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author woutermkievit
 */
public class HibernateSessionHandler {
    private static HibernateSessionHandler THIS=null;
    
    public static HibernateSessionHandler get(){
        if(THIS==null){
            THIS=new HibernateSessionHandler();
        }
        return THIS;
    }
    
    public List executeHQLQuery(String hql) {
        List resultList = null;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q = session.createQuery(hql);
            resultList = q.list();
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return resultList;
    }
    public<T> T getObject(String value,String column,String type){
        String query="from "+type+" where "+column+" = '"+value+"'";
        System.out.println(query);
        return (T)executeHQLQuery(query).get(0);
    }   

}
