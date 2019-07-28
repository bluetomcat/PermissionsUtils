package com.tomcat.lib.interfaces;

/**
 * 创建者：   TomCat0916
 * 创建时间:  2019/7/28
 * 功能描述：  懒加载接口
 */
@FunctionalInterface
public interface Lazy<T> {
    T get();
}
