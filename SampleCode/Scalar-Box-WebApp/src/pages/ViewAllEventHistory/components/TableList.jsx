import React, { useState } from "react";



import { styled, useTheme } from "@mui/material/styles";
import Paper from "@mui/material/Paper";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import DataAndTime from "../../../common/DataAndTime";

import { useTranslation } from "react-i18next";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    fontSize: 20,
    backgroundColor: "#0061D5",
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
    backgroundColor:"#ffffff",
  },
}));



const TableList = ({ dataList }) => {
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  if (!dataList) {
    dataList = [];
  }

  const {t,i18n} = useTranslation();

  const columns2 = [
    { id: "itemName", label: t("viewAllEventHistoryScreenItemNameText"), minWidth: 180, align: "left" },
    { id: "EventType", label: t("viewAllEventHistoryScreenEventTypeText"), minWidth: 150, align: "left" },
    { id: "performedBy", label: t("viewAllEventHistoryScreenPerformedByText"), minWidth: 150, align: "left" },
    { id: "eventOccured", label: t("viewAllEventHistoryScreenEventOccuredText"), minWidth: 150, align: "left" },
  ];

  function convertUtcToLocal(utcString) {
    // Parse the UTC string
    const year = utcString.slice(0, 4);
    const month = utcString.slice(4, 6);
    const day = utcString.slice(6, 8);
    const hour = utcString.slice(8, 10);
    const minute = utcString.slice(10, 12);
    const second = utcString.slice(12, 14);

    // Create a Date object with UTC values
    const utcDate = new Date(Date.UTC(year, month - 1, day, hour, minute, second));

    // Convert UTC date to local date and time strings
    const optionsDate = { year: 'numeric', month: '2-digit', day: '2-digit' };
    const optionsTime = { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false };
    
    const localDateString = utcDate.toLocaleString(undefined, optionsDate);
    const localTimeString = utcDate.toLocaleString(undefined, optionsTime);

    // Combine date and time strings
    const localDateTimeString = `${localDateString} ${localTimeString}`;

    return localDateTimeString;
}



  return (
    <div className="main-container">

      <Paper
        style={{
          width: "100%",
          overflow: "hidden",
          borderRadius: "25px",
          backgroundColor: "#ffffff",
        }}
      >
        {dataList && dataList.length === 0 ? (
          <>

            <TableContainer
              style={{
                width: "100%",
                overflow: "hidden",
              }}
            >
              <Table stickyHeader aria-label="sticky table">
                <TableHead>
                  <TableRow>
                    {columns2.map((column) => (
                      <StyledTableCell
                        key={column.id}
                        align={"left"}
                        style={{ minWidth: column.minWidth, fontSize: "medium", paddingLeft: column.id === "itemName" ? "20px" : undefined, }}
                      >
                        {column.label}
                      </StyledTableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                  <TableRow>
                    <TableCell colSpan={columns2.length}>
                      <div
                        style={{
                          display: "flex",
                          justifyContent: "center",
                          alignItems: "center",
                          minHeight: "400px", // Adjust height as needed
                        }}
                      >
                        <p className="text-xl font-bold">
                          {t("viewAllEventHistoryScreenEmptyListText")}
                        </p>
                      </div>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </TableContainer>
          </>
        ) : (
          <>
            <TableContainer style={{ maxHeight: 440 }}>
              <Table stickyHeader aria-label="sticky table">
                <TableHead>
                  <TableRow>
                    {columns2.map((column) => (
                      <StyledTableCell
                        key={column.id}
                        align={"left"}
                        style={{
                          minWidth: column.minWidth,
                          fontSize: column.fontSize || "medium",
                          paddingLeft: column.id === "itemName" ? "20px" : undefined,
                        }}
                      >
                        {column.label}
                      </StyledTableCell>
                    ))}

                  </TableRow>
                </TableHead>
                <TableBody>
                  {dataList &&
                    dataList
                      .slice(
                        page * rowsPerPage,
                        page * rowsPerPage + rowsPerPage
                      )
                      .map((item, index) => (
                        <TableRow key={index}>
                          <TableCell align="left" style={{ paddingLeft: "20px" }}>
                          <div style={{ wordWrap: "break-word" }}>{item.itemName}</div>
                          </TableCell>
                          <TableCell align="left">
                            {item.eventType}
                          </TableCell>
                          <TableCell align="left">
                            {item.eventCreatedUserName}
                          </TableCell>
                          <TableCell align="left">
                            {/* {item.eventCreatedAt} */}
                          {/* {convertUtcToLocal(item.eventCreatedAt)} */}
                          <DataAndTime dataAndTime={item.eventCreatedAt} />
                          </TableCell>
                        </TableRow>
                      ))}
                </TableBody>
              </Table>
            </TableContainer>

            <TablePagination
              rowsPerPageOptions={[10, 25, 100]}
              component="div"
              count={dataList.length}
              rowsPerPage={rowsPerPage}
              labelRowsPerPage={t("tablePaginationTitleText")}
              page={page}
              onPageChange={handleChangePage}
              onRowsPerPageChange={handleChangeRowsPerPage}
              labelDisplayedRows={({ from, to, count }) => {
                if(i18n.language == "ja"){
                  return `全アイテム  ${count} ${t("tablePageOffText")} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(count / rowsPerPage)})`;
                }

                return `${from}-${to} ${t("tablePageOffText")} ${count} (${Math.ceil(
                  count / rowsPerPage
                )} ${t("tablePageText")})`;
              }}
            />
          </>
        )}
      </Paper>

    </div>
  );
};

export default TableList;
