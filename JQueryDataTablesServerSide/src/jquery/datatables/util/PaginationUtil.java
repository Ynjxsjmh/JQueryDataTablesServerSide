package jquery.datatables.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
}
