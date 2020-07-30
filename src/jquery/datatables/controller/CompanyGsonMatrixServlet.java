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

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import jquery.datatables.model.Company;
import jquery.datatables.model.DataRepository;
import jquery.datatables.model.JQueryDataTablesSentParamModel;
import jquery.datatables.util.DataTablesParamUtil;

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
        int recordsTotal;         // total number of records (unfiltered)
        int recordsFiltered;      // total number of records (filtered)
        JsonArray data = new JsonArray(); // data that will be shown in the table

        recordsTotal = DataRepository.GetCompanies().size();
        List<Company> companies = new LinkedList<Company>();
        for (Company c : DataRepository.GetCompanies()) {
            if (c.getName().toLowerCase().contains(param.getSearch().getValue().toLowerCase())
                    || c.getAddress().toLowerCase().contains(param.getSearch().getValue().toLowerCase())
                    || c.getTown().toLowerCase().contains(param.getSearch().getValue().toLowerCase())) {
                companies.add(c); // add company that matches given search criterion
            }
        }
        recordsFiltered = companies.size(); // number of companies that match search criterion should be returned

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