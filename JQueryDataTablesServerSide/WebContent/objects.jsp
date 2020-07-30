<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="jquery.datatables.model.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Using JQuery DataTables plugin with AJAX source implemented in Java web application</title>
        <link href="media/dataTables/demo_page.css" rel="stylesheet" type="text/css" />
        <link href="media/dataTables/jquery.dataTables.min.css" rel="stylesheet" type="text/css" />
        <script src="scripts/jquery.js" type="text/javascript"></script>
        <script src="scripts/jquery.dataTables.min.js" type="text/javascript"></script>
        <script type="text/javascript">
			$(document).ready(function () {
			    $("#companies").dataTable({
			        "serverSide": true,
			        "ajax": "CompanyGsonObjects",
			        "processing": true,
			        "paginationType": "full_numbers",
			        "columns": [
			                      { "data": "name" },
			                      { "data": "address" },
			                      { "data": "town" }
			                  ]
			    });
			});
        </script>
    </head>
    <body id="dt_example">
        <div id="container">
            <jsp:include page="includes/nav.html"></jsp:include>

            <div id="demo_jui">
		        <table id="companies" class="display">
		            <thead>
		                <tr>
		                    <th>Company name</th>
		                    <th>Address</th>
		                    <th>Town</th>
		                </tr>
		            </thead>
		            <tbody>
		            </tbody>
		        </table>
		    </div>

        </div>
    </body>
</html>