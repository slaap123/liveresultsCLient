/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author woutermkievit
 */
public class HibernateSessionHandler {

    private static HibernateSessionHandler THIS = null;

    public static HibernateSessionHandler get() {
        if (THIS == null) {
            THIS = new HibernateSessionHandler();
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

    public <T> T getObject(String values, String columns, String type) {
        String query = "from " + type + " where ";
        for (int i = 0; i < columns.split(",").length; i++) {
            String column = columns.split(",")[i];
            String value = values.split(",")[i];
            query += (i == 0 ? " " : " AND ") + column + " = '" + value + "'";
        }

        System.out.println(query);
        return (T) executeHQLQuery(query).get(0);
    }
    public <T> T getObject(Class<T> T ,String values, String columns) {
        return this.getObject(T,values.split(","),columns.split(","));
    }
    
    public <T> T getObject(Class<T> T ,Object[] values, String[] columns) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Criteria criteria = session.createCriteria(T);
            for (int i = 0; i < columns.length; i++) {
                criteria.add(Restrictions.eq(columns[i], values[i]));
            }
            session.getTransaction().commit();
            List list = criteria.list();
            System.out.println("found:"+list.size()+" "+T.getName());
            return ((T) criteria.uniqueResult());
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return null;
        
    }

}
