package cc.onetos.hsueh.arrayanimationtest;

/**
 * Created with Android Studio.
 *
 * @author Hsueh
 * @email hsueh@onetos.cc
 * @date 2017-12-29  11:18
 */


import android.widget.ImageView;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;

import cc.onetos.hsueh.arrayanimationtest.frameloader.Process;

/**
 * Project name : DurianObjectPool
 * Created by zhibao.liu on 2016/1/8.
 * Time : 16:06
 * Email warden_sprite@foxmail.com
 * Action : durian
 */
public class ObjectPool<T> {

    //预计对象池的大小
    private int mNumObjects = 10;
    //预计对象池最大数量
    private int mMaxObjects = 50;
    //存放对象池中对象的向量(PoolObject类型)
    private Vector objects = null;

    //创建一个对象池
    public synchronized void createPool(Process process, ImageView tag) {

        //确保对象池没有创建,如果创建好了,就直接返回
        if (objects != null) {
            return;
        }

        //创建保存对象的向量,初始化时有0个元素
        objects = new Vector();

        //根据mNumObjects中设置的值,循环创建指定数目对象
        for (int x = 0; x < mNumObjects; x++) {

            if ((objects.size() == 0) && this.objects.size() < this.mMaxObjects) {
//                Object obj = new Object();
                process = new Process();
                objects.addElement(new PooledObject(process));
            }

        }

    }

    public synchronized Object getObject() {

        //确保对象池已经被创建
        if (objects == null) {
            //对象池还没有创建
            return null;
        }

        //获得一个可用对象
        Object conn = getFreeObject();

        //如果目前没有可以使用对象,即所有的对象都在使用
        while (conn == null) {

            wait(250);
            //重新再试,直到获得可用的对象
            conn = getFreeObject();
        }

        //返回可用的
        return conn;

    }

    /**
     * 本函数从对象池对象 objects 中返回一个可用的的对象，如果
     * 当前没有可用的对象，则创建几个对象，并放入对象池中。
     * 如果创建后，所有的对象都在使用中，则返回 null
     */
    private Object getFreeObject() {

        // 从对象池中获得一个可用的对象
        Object obj = findFreeObject();

        if (obj == null) {

            //这个地方是向对象池增加一个新的对象
            //网上面的这句话有问题,似乎不正确
            createObjects();
            //重新从池中查找是否可用的对象
            obj = findFreeObject();

            // 如果创建对象后仍获得不到可用的对象，则返回 null
            if (obj == null) {
                return null;
            }

        }

        return obj;

    }

    private void createObjects(){

//        Object obj = new Object();
        Process obj=new Process();
        objects.addElement(new PooledObject(obj));
        mNumObjects++;

    }

    /**
     * 查找对象池中所有的对象，查找一个可用的对象，
     * 如果没有可用的对象，返回 null
     */
    public Object findFreeObject() {
        Object obj = null;
        PooledObject pObj = null;

        // 获得对象池向量中所有的对象
        Enumeration enumeration = objects.elements();

        // 遍历所有的对象，看是否有可用的对象
        while (enumeration.hasMoreElements()) {
            pObj = (PooledObject) enumeration.nextElement();
            // 如果此对象不忙，则获得它的对象并把它设为忙
            if (!pObj.isBusy()) {
                obj = pObj.getObjection();
                pObj.setBusy(true);
            }

        }

        return obj;

    }

    /**
     * 此函数返回一个对象到对象池中，并把此对象置为空闲。
     * 所有使用对象池获得的对象均应在不使用此对象时返回它。
     */
    public void returnObjection(Object obj) {

        if (objects == null) {
            return;
        }

        PooledObject pObj = null;

        Enumeration enumeration = objects.elements();

        // 遍历对象池中的所有对象，找到这个要返回的对象对象
        while (enumeration.hasMoreElements()) {
            pObj = (PooledObject) enumeration.nextElement();

            // 先找到对象池中的要返回的对象对象
            if (obj == pObj.getObjection()) {
                pObj.setBusy(false);
                break;
            }

        }

    }

    /**
     * 关闭对象池中所有的对象，并清空对象池。
     */
    public synchronized void closeObjectPool() {

        if (objects == null) {
            return;
        }

        PooledObject pObj = null;

        Enumeration enumeration = objects.elements();

        while (enumeration.hasMoreElements()) {
            pObj = (PooledObject) enumeration.nextElement();
            if (pObj.isBusy()) {

                wait(5000);

            }

            objects.removeElement(pObj);

        }

        objects = null;

    }

    private void wait(int seconds) {

        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 内部使用的用于保存对象池中对象的类。
     * 此类中有两个成员，一个是对象，另一个是指示此对象是否正在使用的标志 。
     */
    class PooledObject {

        Object objection = null;
        boolean busy = false;

        public PooledObject(Object objection) {
            this.objection = objection;
        }

        public Object getObjection() {
            return objection;
        }

        public void setObjection(Object objection) {
            this.objection = objection;
        }

        public boolean isBusy() {
            return busy;
        }

        public void setBusy(boolean busy) {
            this.busy = busy;
        }


    }

}
