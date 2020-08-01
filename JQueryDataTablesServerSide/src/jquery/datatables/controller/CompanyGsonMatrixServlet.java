package jquery.datatables.controller;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jquery.datatables.model.Company;
import jquery.datatables.model.DataRepository;
import jquery.datatables.model.JQueryDataTablesSentParamModel;
import jquery.datatables.util.DataTablesParamUtil;
import jquery.datatables.util.PaginationUtil;

/**
 * CompanyServlet provides data to the JQuery DataTables
 */
public class CompanyGsonMatrixServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public CompanyGsonMatrixServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JQueryDataTablesSentParamModel param = DataTablesParamUtil.getParam(request);

        System.out.println("CompanyGsonMatrixServlet.doGet() [param=" + param + "]");

        int draw = param.getDraw();
        JsonArray data = new JsonArray(); // data that will be shown in the table

        List<Company> companies = DataRepository.GetCompanies();
        companies = PaginationUtil.logicalFilterCompanies(param, companies);

        int recordsTotal = DataRepository.GetCompanies().size();  // total number of records (unfiltered)
        int recordsFiltered = companies.size();                   // total number of records (filtered)

        companies = PaginationUtil.logicalSort(param, companies);
        companies = PaginationUtil.logicalLimit(param, companies);

        try {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("draw", draw);
            jsonResponse.addProperty("recordsTotal", recordsTotal);
            jsonResponse.addProperty("recordsFiltered", recordsFiltered);

            for (Company c : companies) {
                JsonArray row = new JsonArray();
                row.add(new JsonPrimitive(c.getName()));
                row.add(new JsonPrimitive(c.getAddress()));
                row.add(new JsonPrimitive(c.getTown()));
                data.add(row);
            }
            jsonResponse.add("data", data);

            response.setContentType("application/Json");
            response.getWriter().print(jsonResponse.toString());

            System.out.println("CompanyGsonMatrixServlet.doGet() [jsonResponse=" + jsonResponse.toString() + "]");
        } catch (JsonIOException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().print(e.getMessage());
        }

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

}