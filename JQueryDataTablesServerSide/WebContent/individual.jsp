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
			    var table = $("#companies").DataTable({
			        "serverSide": true,
			        "ajax": {
			            "url": "CompanyIndividualColumnSearching",
			        },
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
			        ],
			        initComplete: function (settings, json) {
			            // Apply the search
			            this.api().columns([0,1]).every( function () {
			                var column = this;
			                var title = $(column.footer()).text();
			                $( '<input type="text" placeholder="Search '+title+'" />' )
			                	.appendTo( $(column.footer()).empty() )
    			                .on( 'keyup change clear', function () {
    			                    if ( column.search() !== this.value ) {
    			                    	column
    			                            .search( this.value )
    			                            .draw();
    			                    }
    			                } );

			            } );

			            this.api().columns([2]).every( function () {
			                var column = this;
			                var title = $(column.footer()).text();
			                var select = $('<select><option value="">Select ' + title + '</option></select>')
			                    .appendTo( $(column.footer()).empty() )
			                    .on( 'change', function () {
			                        var val = $.fn.dataTable.util.escapeRegex(
			                            $(this).val()
			                        );

			                        column
                                        .search( val ? '^'+val+'$' : '', true, false )
			                            .draw();
			                    } );

			                var colIdx = column.index();

			                Array.from(new Set(json["data_" + colIdx].sort())).forEach( function ( d ) {
			                	select.append( '<option value="'+d+'">'+d+'</option>' )
			                } );
			            } );
			        }
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
                    <tfoot>
                        <tr>
		                    <th>Company name</th>
		                    <th>Address</th>
		                    <th>Town</th>
                        </tr>
                    </tfoot>
		        </table>
		    </div>

        </div>
    </body>
</html>