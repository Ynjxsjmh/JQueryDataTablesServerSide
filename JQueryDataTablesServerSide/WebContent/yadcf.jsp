<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="jquery.datatables.model.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Using Yadcf plugin with AJAX source implemented in Java web application</title>
        <link href="media/dataTables/demo_page.css" rel="stylesheet" type="text/css" />
        <link href="media/dataTables/jquery-ui.min.css" rel="stylesheet" type="text/css" />
        <link href="media/dataTables/select2.css" rel="stylesheet" type="text/css" />
        <link href="media/dataTables/chosen.min.css" rel="stylesheet" type="text/css" />
        <link href="media/dataTables/jquery.dataTables.min.css" rel="stylesheet" type="text/css" />
        <link href="media/dataTables/jquery.dataTables.yadcf.css" rel="stylesheet" type="text/css" />

        <script src="scripts/jquery.js" type="text/javascript"></script>
        <script src="scripts/jquery-ui.min.js" type="text/javascript"></script>
        <script src="scripts/select2.js" type="text/javascript"></script>
        <script src="scripts/chosen.jquery.min.js" type="text/javascript"></script>
        <script src="scripts/jquery.dataTables.min.js" type="text/javascript"></script>
        <script src="scripts/jquery.dataTables.yadcf.js" type="text/javascript"></script>

        <script type="text/javascript">
			$(document).ready(function () {
				let oTable = $("#companies").DataTable({
			        "serverSide": true,
			        "ajax": "Yadcf",
			        "processing": true,
			        "paginationType": "full_numbers",
			        "columns": [
	                      { "data": "name" },
	                      { "data": "address" },
	                      { "data": "town" }
	                ],
			        "columnDefs": [
	                      { "name": "name",    "targets": 0 },
	                      { "name": "address", "targets": 1 },
	                      { "name": "town",    "targets": 2 }
			        ]
			    });

		        yadcf.init(oTable, [
		            {
		                column_number: 0,
		                filter_type: "auto_complete"
		            },
		            {
		                column_number: 1,
		                filter_type: "text",
		                filter_delay: 500
		            },
		            {
		                column_number: 2,
		                select_type: "select2",
		                select_type_options: {
		                    width: '150px',
		                }
		            },
		        ]);
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