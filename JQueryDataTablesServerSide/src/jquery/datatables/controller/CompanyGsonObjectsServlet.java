package jquery.datatables.controller;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import jquery.datatables.model.Company;
import jquery.datatables.model.DataRepository;
import jquery.datatables.model.JQueryDataTablesReturnedDataModel;
import jquery.datatables.model.JQueryDataTablesSentParamModel;
import jquery.datatables.util.DataTablesParamUtil;
import jquery.datatables.util.PaginationUtil;

/**
 * CompanyServlet provides data to the JQuery DataTables
 */
public class CompanyGsonObjectsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public CompanyGsonObjectsServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        JQueryDataTablesSentParamModel param = DataTablesParamUtil.getParam(request);

        System.out.println("CompanyGsonObjectsServlet.doGet() [param=" + new Gson().toJson(param) + "]");

        int draw = param.getDraw();

        List<Company> companies = DataRepository.GetCompanies();
        companies = PaginationUtil.logicalFilter(param, companies);

        int recordsTotal = DataRepository.GetCompanies().size();  // total number of records (unfiltered)
        int recordsFiltered = companies.size();                   // total number of records (filtered)

        companies = PaginationUtil.logicalSort(param, companies);
        companies = PaginationUtil.logicalLimit(param, companies);

        try {
            JQueryDataTablesReturnedDataModel<Company> returned = new JQueryDataTablesReturnedDataModel<>();
            returned.setDraw(draw);
            returned.setRecordsTotal(recordsTotal);
            returned.setRecordsFiltered(recordsFiltered);
            returned.setData(companies);

            String returnedData = new Gson().toJson(returned);

            response.setContentType("application/Json");
            response.getWriter().print(returnedData);

            System.out.println("CompanyGsonObjectsServlet.doGet() [returnedData=" + returnedData + "]");
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