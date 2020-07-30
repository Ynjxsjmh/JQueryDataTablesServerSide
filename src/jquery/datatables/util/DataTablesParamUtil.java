package jquery.datatables.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import jquery.datatables.model.JQueryDataTablesColumn;
import jquery.datatables.model.JQueryDataTablesOrder;
import jquery.datatables.model.JQueryDataTablesSearch;
import jquery.datatables.model.JQueryDataTablesSentParamModel;

public class DataTablesParamUtil {

    public static JQueryDataTablesSentParamModel getParam(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();

        if(parameterNames.hasMoreElements()) {
            JQueryDataTablesSentParamModel param = new JQueryDataTablesSentParamModel();

            param.setDraw(request.getParameter("draw"));
            param.setStart(Integer.parseInt(request.getParameter("start")));
            param.setLength(Integer.parseInt(request.getParameter("length")));

            JQueryDataTablesSearch globalSearch = new JQueryDataTablesSearch();
            globalSearch.setValue(request.getParameter("search[value]"));
            globalSearch.setRegex(Boolean.parseBoolean(request.getParameter("search[regex]")));
            param.setSearch(globalSearch);

            List<JQueryDataTablesOrder> orders = new ArrayList<>();
            int orderCnt = DataTablesParamUtil.getNumberOfArray(request, "order");

            for (int i = 0; i < orderCnt; i++) {
                JQueryDataTablesOrder order = new JQueryDataTablesOrder();

                order.setColumn(Integer.parseInt(request.getParameter("order[" + i + "][column]")));
                order.setDir(request.getParameter("order[" + i + "][dir]"));

                orders.add(order);
            }

            param.setOrder(orders);

            List<JQueryDataTablesColumn> columns = new ArrayList<>();
            int columnCnt = DataTablesParamUtil.getNumberOfArray(request, "columns");

            for (int i = 0; i < columnCnt; i++) {
                JQueryDataTablesColumn column = new JQueryDataTablesColumn();
                column.setData(request.getParameter("columns[" + i + "][data]"));
                column.setName(request.getParameter("columns[" + i + "][name]"));
                column.setSearchable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][searchable]")));
                column.setOrderable(Boolean.parseBoolean(request.getParameter("columns[" + i + "][orderable]")));

                JQueryDataTablesSearch search = new JQueryDataTablesSearch();
                search.setValue(request.getParameter("columns[" + i + "][search][value]"));
                search.setRegex(Boolean.parseBoolean(request.getParameter("columns[" + i + "][search][regex]")));

                column.setSearch(search);

                columns.add(column);
            }

            param.setColumns(columns);

            return param;
        } else {
            return null;
        }
    }

    private static int getNumberOfArray(HttpServletRequest request, String arrayName) {
        Pattern pattern = null;

        switch (arrayName) {
        case "columns":
            pattern = Pattern.compile("columns\\[[0-9]+\\]\\[data\\]");
            break;
        case "order":
            pattern = Pattern.compile("order\\[[0-9]+\\]\\[column\\]");
            break;
        default:
            break;
        }

        @SuppressWarnings("rawtypes")
        Enumeration params = request.getParameterNames();
        List<String> array = new ArrayList<String>();

        while (params.hasMoreElements()) {
            String paramName = (String) params.nextElement();
            Matcher matcher = pattern.matcher(paramName);
            if (matcher.matches()) {
                array.add(paramName);
            }
        }

        return array.size();
    }

}
