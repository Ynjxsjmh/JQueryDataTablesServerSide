package jquery.datatables.controller;

import java.io.IOException;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
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
        int recordsTotal;         // total number of records (unfiltered)
        int recordsFiltered;      // total number of records (filtered)

        recordsTotal = DataRepository.GetCompanies().size();
        List<Company> companies = new LinkedList<Company>();

        for (Company c : DataRepository.GetCompanies()) {
            if (c.getName().toLowerCase().contains(param.getSearch().getValue().toLowerCase())
                    || c.getAddress().toLowerCase().contains(param.getSearch().getValue().toLowerCase())
                    || c.getTown().toLowerCase().contains(param.getSearch().getValue().toLowerCase())) {
                companies.add(c); // add company that matches given search criterion
            }
        }
        recordsFiltered = companies.size();  // number of companies that match search criterion should be returned

        final int sortColumnIndex = param.getOrder().get(0).getColumn();
        final int sortDirection = param.getOrder().get(0).getDir().equals("asc") ? -1 : 1;

        Collections.sort(companies, new Comparator<Company>() {
            @Override
            public int compare(Company c1, Company c2) {
                switch (sortColumnIndex) {
                case 0:
                    return c1.getName().compareTo(c2.getName()) * sortDirection;
                case 1:
                    return c1.getAddress().compareTo(c2.getAddress()) * sortDirection;
                case 2:
                    return c1.getTown().compareTo(c2.getTown()) * sortDirection;
                }
                return 0;
            }
        });

        if (companies.size() < param.getStart() + param.getLength()) {
            companies = companies.subList(param.getStart(), companies.size());
        } else {
            companies = companies.subList(param.getStart(), param.getStart() + param.getLength());
        }

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