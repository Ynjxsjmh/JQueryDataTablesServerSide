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
                        "data": function ( d ) {
                            d.company = $("#company_filter").val();
                            d['with space'] = $("#company_filter").val();
                        },
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
                        var column = this.api().column(2);
                        var title = $(column.footer()).text();
                        var select = $('<select><option value="">Select ' + title + '</option></select>')
                            .appendTo( $('#town').empty().text('Town: ') )
                            .on( 'change', function () {
                                var val = $(this).val();
                                column
                                    .search( val )
                                    .draw();
                            } );

                        var colIdx = column.index();
                        Array.from(new Set(json["data_" + colIdx].sort())).forEach( function ( d ) {
                            select.append( '<option value="'+d+'">'+d+'</option>' )
                        } );
                    }
                });

                $('input.address_filter').on( 'keyup', function () {
                    table.column(1).search( this.value ).draw();
                } );

                $('input.company_filter').on( 'keyup', function () {
                    table.draw();
                } );
            });
        </script>
    </head>
    <body id="dt_example">
        <div id="container">
            <jsp:include page="includes/nav.html"></jsp:include>

            <div id="demo_jui">
                <div>
                    <p>Company Search: <input type="text" class="company_filter" id="company_filter"></p>
                    <p>Address Search: <input type="text" class="address_filter" id="address_filter"></p>
                    <span id="town"></span>
                </div>

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

                <div>
                    Related DataTables document:
                    <ul>
                        <li>
                            <a href="https://datatables.net/reference/option/ajax">ajax</a>
                        </li>
                        <li>
                            <a href="https://datatables.net/reference/api/draw()">draw()</a>
                        </li>
                        <li>
                            <a href="https://datatables.net/examples/api/regex.html">Search API (regular expressions)</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </body>
</html>