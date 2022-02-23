package com.jinkeen.base.util;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ActivityManager {

    private final static Set<AppCompatActivity> activitys = new HashSet<>();

    /**
     * 向<code>AppCompatActivity</code>管理池中添加一个新的{@link AppCompatActivity}对象
     *
     * @param activity 新的{@link AppCompatActivity}对象
     */
    public static void add(AppCompatActivity activity) {
        activitys.add(activity);
    }

    /**
     * 从<code>AppCompatActivity</code>管理池中移除一个指定的{@link AppCompatActivity}对象
     *
     * @param activity 要被移除的{@link AppCompatActivity}对象
     */
    public static void remove(AppCompatActivity activity) {
        activitys.remove(activity);
    }

    /**
     * 结束当前管理池中所有未销毁的{@link AppCompatActivity}对象
     */
    public static void finishAll() {
        for (AppCompatActivity activity : activitys) {
            if (!activity.isFinishing() || !activity.isDestroyed())
                activity.finish();
        }
    }

    /**
     * 根据指定的类名来结束一个正在运行中的{@link AppCompatActivity}对象
     *
     * @param clazz {@link AppCompatActivity}的类对象
     */
    public static void finishSingle(Class<? extends AppCompatActivity> clazz) {
        for (AppCompatActivity activity : activitys) {
            if (activity.getClass().isAssignableFrom(clazz)) activity.finish();
        }
    }

    /**
     * 结束正在运行中的<code>AppCompatActivity</code>集合
     *
     * @param clazzs 多个{@link AppCompatActivity}的类对象
     */
    @SafeVarargs
    public static void finishActivitysByClass(Class<? extends AppCompatActivity>... clazzs) {
        final List<Class<? extends AppCompatActivity>> cs = Arrays.asList(clazzs);
        for (AppCompatActivity activity : activitys) {
            if (cs.contains(activity.getClass()))
                if (!activity.isFinishing() || !activity.isDestroyed())
                    activity.finish();
        }
    }

    /**
     * 结束其他所有的<code>AppCompatActivity</code>，但只留下必要的其中一个
     *
     * @param clazz 要被保留的<code>AppCompatActivity</code>对象
     */
    public static void finishOthersKeepone(Class<? extends AppCompatActivity> clazz) {
        for (AppCompatActivity activity : activitys) {
            if (!activity.getClass().isAssignableFrom(clazz)) activity.finish();
        }
    }

    /**
     * 结束其他所有的<code>AppCompatActivity</code>，但只留下必要的其中几个
     *
     * @param clazzs 要被保留的<code>AppCompatActivity</code>对象
     */
    @SafeVarargs
    public static void finishOthersKeepMultiple(Class<? extends AppCompatActivity>... clazzs) {
        final List<Class<? extends AppCompatActivity>> cs = Arrays.asList(clazzs);
        for (AppCompatActivity activity : activitys) {
            if (!cs.contains(activity.getClass()))
                if (!activity.isFinishing() || !activity.isDestroyed())
                    activity.finish();
        }
    }
}
