package jquery.datatables.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import jquery.datatables.model.Company;
import jquery.datatables.model.DataRepository;
import jquery.datatables.model.JQueryDataTablesReturnedDataModel;
import jquery.datatables.model.JQueryDataTablesSentParamModel;
import jquery.datatables.util.DataTablesParamUtil;
import jquery.datatables.util.PaginationUtil;

public class YadcfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JQueryDataTablesSentParamModel param = DataTablesParamUtil.getParam(request);

        System.out.println("YadcfServlet.doGet() [param=" + new Gson().toJson(param) + "]");

        List<Company> companies = DataRepository.GetCompanies();
        companies = PaginationUtil.logicalFilter(param, companies);

        int recordsTotal = DataRepository.GetCompanies().size();  // total number of records (unfiltered)
        int recordsFiltered = companies.size();                   // total number of records (filtered)

        companies = PaginationUtil.logicalSort(param, companies);
        companies = PaginationUtil.logicalLimit(param, companies);

        Set<String> col0Data = new HashSet<>();
        Set<String> col2Data = new HashSet<>();
        for (Company c : DataRepository.GetCompanies()) {
            col0Data.add(c.getName());
            col2Data.add(c.getTown());
        }

        try {
            JQueryDataTablesReturnedDataModel<Company> returned = new JQueryDataTablesReturnedDataModel<>();
            returned.setDraw(param.getDraw());
            returned.setRecordsTotal(recordsTotal);
            returned.setRecordsFiltered(recordsFiltered);
            returned.setData(companies);

            JsonElement jsonElement = new Gson().toJsonTree(returned);
            JsonObject jsonResponse = (JsonObject) jsonElement;
            jsonResponse.add("yadcf_data_0", new Gson().toJsonTree(col0Data));
            jsonResponse.add("yadcf_data_2", new Gson().toJsonTree(col2Data));

            response.setContentType("application/Json");
            response.getWriter().print(jsonResponse);

            System.out.println("YadcfServlet.doGet() [jsonResponse=" + jsonResponse + "]");
        } catch (JsonIOException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().print(e.getMessage());
        }
    }

}
