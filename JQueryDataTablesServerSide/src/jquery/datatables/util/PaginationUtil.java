package jquery.datatables.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.CompareToBuilder;

import jquery.datatables.model.JQueryDataTablesColumn;
import jquery.datatables.model.JQueryDataTablesOrder;
import jquery.datatables.model.JQueryDataTablesSentParamModel;

public class PaginationUtil {
    /** The Constant BLANK. */
    private static final String BLANK = "";

    /** The Constant SPACE. */
    private static final String SPACE = " ";

    /** The Constant LIKE_PREFIX. */
    private static final String LIKE_PREFIX = " LIKE '%";

    /** The Constant LIKE_SUFFIX. */
    private static final String LIKE_SUFFIX = "%' ";

    /** The Constant AND. */
    private static final String AND = " AND ";

    /** The Constant OR. */
    private static final String OR = " OR ";

    private static final String PAREN_OPN = " ( ";

    private static final String PAREN_CLS = " ) ";

    /** The Constant COMMA. */
    private static final String COMMA = " , ";

    public static <T> List<T> logicalFilter(JQueryDataTablesSentParamModel param, List<T> beforeFiltered) {
        List<JQueryDataTablesColumn> columns = param.getColumns();

        List<T> afterFiltered = new ArrayList<>();

        for (T item : beforeFiltered) {
            boolean isSatisfied = true;

            for (JQueryDataTablesColumn column : columns) {
                if (column.isSearchable()) {
                    String columnName = column.getName();
                    String searchValue = column.getSearch().getValue();

                    String columnValue = getFieldValueByFieldName(columnName, item);

                    isSatisfied = isSatisfied && columnValue.contains(searchValue);
                }
            }

            if (isSatisfied) {
                afterFiltered.add(item);
            }
        }

        beforeFiltered = afterFiltered;
        afterFiltered = new ArrayList<>();

        /* Global filter
         * If there exists at least one column contains the search value,
         * the object should be returned.
         */
        String globalSearchValue = param.getSearch().getValue();
        for (T item : beforeFiltered) {
            boolean isSatisfied = false;

            for (JQueryDataTablesColumn column : columns) {
                if (column.isSearchable()) {
                    String columnName = column.getName();

                    String columnValue = getFieldValueByFieldName(columnName, item);

                    isSatisfied = isSatisfied || columnValue.contains(globalSearchValue);
                }
            }

            if (isSatisfied) {
                afterFiltered.add(item);
            }
        }

        return afterFiltered;
    }

    public static <T> List<T> logicalSort(JQueryDataTablesSentParamModel param, List<T> beforeSorted) {
        List<JQueryDataTablesColumn> columns = param.getColumns();
        List<JQueryDataTablesOrder> orders = param.getOrder();
        Map<String, String> sortMap = new HashMap<>();

        for (JQueryDataTablesOrder order : orders) {
            int columnIndex= order.getColumn();
            JQueryDataTablesColumn column = columns.get(columnIndex);

            if (column.isOrderable()) {
                String columnName = column.getName();
                String columnDir = order.getDir();
                sortMap.put(columnName, columnDir);
            }
        }

        Collections.sort(beforeSorted, new Comparator<T>() {
            public int compare(T one, T two) {
                CompareToBuilder compToBuild = new CompareToBuilder();

                Iterator<Entry<String, String>> sbit = sortMap.entrySet().iterator();

                while (sbit.hasNext()) {
                    Map.Entry<String, String> pair =  sbit.next();
                    String fieldName = pair.getKey();
                    String direction = pair.getValue();
                    String fieldValue1 = getFieldValueByFieldName(fieldName, one);
                    String fieldValue2 = getFieldValueByFieldName(fieldName, two);

                    if(direction.toUpperCase().equals("ASC")) {
                        compToBuild.append(fieldValue1, fieldValue2);
                    }
                    if(direction.toUpperCase().equals("DESC")) {
                        compToBuild.append(fieldValue2, fieldValue1);
                    }
                }

                return compToBuild.toComparison();
            }
        });

        return beforeSorted;
    }

    public static <T> List<T> logicalLimit(JQueryDataTablesSentParamModel param, List<T> beforeLimited) {
        List<T> afterLimited;

        if (beforeLimited.size() < param.getStart() + param.getLength()) {
            afterLimited = beforeLimited.subList(param.getStart(), beforeLimited.size());
        } else {
            afterLimited = beforeLimited.subList(param.getStart(), param.getStart() + param.getLength());
        }

        return afterLimited;
    }

    public static String getFilterByClause(JQueryDataTablesSentParamModel param) {
        List<JQueryDataTablesColumn> columns = param.getColumns();
        Map<String, String> filterMap = new HashMap<>();
        Map<String, String> globalfilterMap = new HashMap<>();

        for (JQueryDataTablesColumn column : columns) {
            if (column.isSearchable()) {
                String columnName = column.getName();
                String searchValue = column.getSearch().getValue();
                if ("" != searchValue) {
                    filterMap.put(columnName, searchValue);
                }

                String globalSearchValue = param.getSearch().getValue();
                if ("" != globalSearchValue) {
                    globalfilterMap.put(columnName, globalSearchValue);
                }
            }
        }

        StringBuilder localFilterByClauseBuilder = null;
        StringBuilder globalFilterByClauseBuilder = null;

        if (!filterMap.isEmpty()) {
            Iterator<Entry<String, String>> fbit = filterMap.entrySet().iterator();

            while (fbit.hasNext()) {
                Map.Entry<String, String> pair =  fbit.next();

                if (null == localFilterByClauseBuilder) {
                    localFilterByClauseBuilder = new StringBuilder();

                    localFilterByClauseBuilder.append(PAREN_OPN)
                                              .append(pair.getKey())
                                              .append(LIKE_PREFIX)
                                              .append(pair.getValue())
                                              .append(LIKE_SUFFIX)
                                              .append(PAREN_CLS);
                } else {
                    localFilterByClauseBuilder.append(AND)
                                              .append(PAREN_OPN)
                                              .append(pair.getKey())
                                              .append(LIKE_PREFIX)
                                              .append(pair.getValue())
                                              .append(LIKE_SUFFIX)
                                              .append(PAREN_CLS);
                }
            }
        }

        if (!globalfilterMap.isEmpty()) {
            Iterator<Entry<String, String>> fbit = globalfilterMap.entrySet().iterator();

            while (fbit.hasNext()) {
                Map.Entry<String, String> pair =  fbit.next();

                if (null == globalFilterByClauseBuilder) {
                    globalFilterByClauseBuilder = new StringBuilder();

                    globalFilterByClauseBuilder.append(PAREN_OPN)
                                               .append(pair.getKey())
                                               .append(LIKE_PREFIX)
                                               .append(pair.getValue())
                                               .append(LIKE_SUFFIX)
                                               .append(PAREN_CLS);
                } else {
                    globalFilterByClauseBuilder.append(OR)
                                               .append(PAREN_OPN)
                                               .append(pair.getKey())
                                               .append(LIKE_PREFIX)
                                               .append(pair.getValue())
                                               .append(LIKE_SUFFIX)
                                               .append(PAREN_CLS);
                }
            }
        }

        StringBuilder filterByClauseBuilder = null;

        if (localFilterByClauseBuilder == null && globalFilterByClauseBuilder == null) {
            // Do nothing here
        } else if (localFilterByClauseBuilder != null && globalFilterByClauseBuilder == null) {
            filterByClauseBuilder = localFilterByClauseBuilder;
        } else if (localFilterByClauseBuilder == null && globalFilterByClauseBuilder != null) {
            filterByClauseBuilder = globalFilterByClauseBuilder;
        } else {
            filterByClauseBuilder = new StringBuilder();
            filterByClauseBuilder.append(localFilterByClauseBuilder)
                                 .append(AND)
                                 .append(PAREN_OPN)
                                 .append(globalFilterByClauseBuilder)
                                 .append(PAREN_CLS);
        }

        return (null == filterByClauseBuilder) ? BLANK : filterByClauseBuilder.toString();
    }

    public static String getOrderByClause(JQueryDataTablesSentParamModel param) {
        List<JQueryDataTablesOrder> orders = param.getOrder();
        List<JQueryDataTablesColumn> columns = param.getColumns();
        Map<String, String> orderMap = new HashMap<>();

        for (JQueryDataTablesOrder order : orders) {
            int columnIndex= order.getColumn();
            JQueryDataTablesColumn column = columns.get(columnIndex);

            if (column.isOrderable()) {
                String columnName = column.getName();
                String columnDir = order.getDir();
                orderMap.put(columnName, columnDir);
            }
        }

        StringBuilder orderByClauseBuilder = null;
        if (!orderMap.isEmpty()) {
            Iterator<Entry<String, String>> sbit = orderMap.entrySet().iterator();

            while (sbit.hasNext()) {
                Map.Entry<String, String> pair =  sbit.next();
                if (null == orderByClauseBuilder) {
                    orderByClauseBuilder = new StringBuilder();
                    orderByClauseBuilder.append(pair.getKey())
                                        .append(SPACE)
                                        .append(pair.getValue());
                } else {
                    orderByClauseBuilder.append(COMMA)
                                        .append(pair.getKey())
                                        .append(SPACE)
                                        .append(pair.getValue());
                }
            }
        }

        return (null == orderByClauseBuilder) ? BLANK : orderByClauseBuilder.toString();
    }

    public static String getLimitByClause(JQueryDataTablesSentParamModel param) {
        int pageNum = param.getStart();
        int pageSize = param.getLength();

        return String.format("LIMIT %d, %d", (pageNum-1)*pageSize, pageSize);
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName
     * @param object
     * @return
     */
    private static String getFieldValueByFieldName(String fieldName, Object object) {

        Field field = null;
        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // 因为父类可能没有子类的属性，所以这里什么都不做
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        field.setAccessible(true);

        try {
            Object value = field.get(object);
            return String.valueOf(value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

}
