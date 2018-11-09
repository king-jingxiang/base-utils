package org.base.utils.mapper;

import org.base.utils.paging.SPage;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ConvertUtil {


    /**
     * 基于Dozer转换Collection中对象的类型.
     */
    public static <F, T> List<T> convertList(final Collection<F> sourceList, final Class<T> destinationClass) {
        return convertList(sourceList, e -> BeanMapper.map(e, destinationClass));
    }

    /**
     * 基于自定义函数Collection中对象的类型.
     */
    public static <F, T> List<T> convertList(final Collection<F> sourceList, Function<? super F, ? extends T> mapper) {
        if (sourceList == null) {
            return null;
        }

        List<T> destinationList = Lists.newArrayList();
        for (F sourceObject : sourceList) {
            T destinationObject = mapper.apply(sourceObject);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }


    /**
     * 基于Dozer转换对象的类型.
     */
    public static <F, T> T convert(final F source, final Class<T> destinationClazz) {
        return convert(source, e -> BeanMapper.map(e, destinationClazz));
    }


    /**
     * 基于自定义函数转换.
     */
    public static <F, T> T convert(final F source, Function<? super F, ? extends T> mapper) {
        if (source == null) {
            return null;
        }

        return mapper.apply(source);
    }



    //endregion


    //endregion SPage->SPage. 自己转换

    public static <F, T> SPage<T> convertPage(final SPage<F> pageSource,
                                              final Class<T> destinationClazz) {
        return convertPage(pageSource, destinationClazz, pageSource.getTotal());
    }


    public static <F, T> SPage<T> convertPage(final SPage<F> pageSource,
                                              final Function<? super F, ? extends T> mapper) {
        return convertPage(pageSource, mapper, pageSource.getTotal());
    }

    /**
     * 通过Dozer转换分页数据,并模糊化总条数为.
     *
     * @param maxTotal 当实际条数大于本值时,实际条数被设置为maxTotal
     */
    public static <F, T> SPage<T> convertPage(final SPage<F> pageSource,
                                              final Class<T> destinationClazz, long maxTotal) {

        return convertPage(pageSource, e -> BeanMapper.map(e, destinationClazz), maxTotal);
    }

    /**
     * 通过自定义函数转换分页数据,并模糊化总条数为.
     *
     * @param maxTotal 当实际条数大于本值时,实际条数被设置为maxTotal
     */
    public static <F, T> SPage<T> convertPage(final SPage<F> pageSource,
                                              final Function<? super F, ? extends T> mapper,
                                              final long maxTotal) {

        final List<T> content = ConvertUtil.convertList(pageSource.getContent(), mapper);

        long total = pageSource.getTotal();

        if (total > maxTotal) {
            total = maxTotal;
        }

        return new SPage<T>(content, pageSource.getPage(), pageSource.getSize(),
                total);
    }

}
