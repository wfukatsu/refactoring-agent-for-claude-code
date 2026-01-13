import React, { useEffect, useState } from "react";
import "./AuditorsAndGroups.css";
import { Box, Checkbox } from "@mui/material";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import { styled } from "@mui/material/styles";
import DeleteAuditorConfirm from "./Alerts/DeleteAuditorConfirm";
import DeleteAuditGroupConfirm from "./Alerts/DeleteAuditGroupConfirm";
import EditAuditorConfirm from "./Alerts/EditAuditorConfirm";
import EditGroupConfirm from "./Alerts/EditGroupConfirm";
import { useSelector } from "react-redux";
import AddExternalAuditor from "./Alerts/AddExternalAuditor";
import AddAuditGroup from "./Alerts/AddAuditGroup";
import { BASE_URL } from "../../utils/constants";

import MuiCircularProgress from "@mui/joy/CircularProgress";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#0061D5",
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const AuditorsAndGroups = ({ open }) => {
  const [externalAuditorsData, setExternalAuditorsData] = useState([]);
  const [auditGroupsData, setAuditGroupData] = useState([]);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [selectedRows, setSelectedRows] = useState([]);
  const [selectedRowsTwo, setSelectedRowsTwo] = useState([]);

  const [AuditUsersList, setAudiUsersList] = useState([]);
  const [render, setRender] = useState(0);
  const [selectedButton, setSelectedButton] = useState("button1");
  const [table1, setTable1] = useState(true);
  const [table2, setTable2] = useState(false);

  const [loading, setLoading] = useState(false);
  const axiosPrivate = useAxiosPrivate();

  const { t, i18n } = useTranslation();

  const handleRender = () => {
    setRender(render + 1);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const handleVersionHistory = (button) => {
    setSelectedButton(button);
    setTable1(true);
    setTable2(false);
  };
  const handleFileCopies = (button) => {
    setSelectedButton(button);
    setTable1(false);
    setTable2(true);
  };

  async function getAuditGroupsData() {
    try {
      const response = await axiosPrivate.get(
        `${BASE_URL}/box/auditGroup/getListOfAuditGroup`
      );
      setAuditGroupData(response.data.data);
      setLoading(true);
    } catch (error) {
      console.log("I got the below error");
      console.error(error);
    }
  }
  async function getExternalAuditorsData() {
    try {
      console.log("111111111111111111111111111");
      const response = await axiosPrivate.get(
        `${BASE_URL}/box/user/getListOfExternalAuditors`
      );
      // console.log("------->", response.data.status);
      console.log("1111111111111111111111111111", response.data.data);
      setExternalAuditorsData(response.data.data);
      setAudiUsersList(response.data.data);
      console.log("111111111111111111111111111 222222222222222");
      setLoading(true);
    } catch (error) {
      console.log("111111111111111111111111111I got the below error");
      console.error(error);
    }
  }

  useEffect(() => {
    getExternalAuditorsData().then(() => {
      console.log("1111111111111 33333333333333");
      getAuditGroupsData();
    });

    // setSelectedRows([]);
    // setSelectedRowsTwo([]);
  }, [render]);

  return (
    // <Main open={open}>
    <div style={{ width: "100%" }}>
      <div className="heading">
        <h1 className="font-bold text-xl">
          {t("manageExAuditorsandGroupScreenTitleText")}
        </h1>
        {table1 && (
          <AddExternalAuditor onClick={handleRender}></AddExternalAuditor>
        )}
        {table2 && (
          <AddAuditGroup
            AuditUsersList={AuditUsersList}
            onClick={handleRender}
          ></AddAuditGroup>
        )}
      </div>

      <div
        className="btn "
        style={{ paddingTop: "10px", paddingBottom: "5px" }}
      >
        <button
          className={`btn ${
            selectedButton === "button1" ? "button1" : "default"
          }`}
          style={{ width: "250px", height: "55px" }}
          onClick={() => {
            handleVersionHistory("button1");
            setSelectedRows([]);
          }}
        >
          {" "}
          {t("manageExAuditorScreenText")}{" "}
        </button>
        <button
          className={`btn ${
            selectedButton === "button2" ? "button2" : "default"
          }`}
          style={{ width: "250px", height: "55px" }}
          onClick={() => {
            handleFileCopies("button2");
            setSelectedRowsTwo([]);
          }}
        >
          {" "}
          {t("manageExGroupScreenText")}{" "}
        </button>
      </div>

      {table1 && (
        <div>
          {loading ? (
            <Paper
              sx={{ width: "100%", overflow: "hidden", borderRadius: "20px" }}
            >
              {externalAuditorsData.length === 0 ? (
                <TableContainer sx={{ maxHeight: 450 }}>
                  <Table stickyHeader aria-label="sticky table">
                    <TableHead>
                      <TableRow>
                        {/* <StyledTableCell
                          style={{ width: "5%", textAlign: "left" }}
                        >
                          Select
                        </StyledTableCell> */}
                        <StyledTableCell
                          style={{
                            paddingLeft: "20px",
                            width: "10%",
                            textAlign: "left",
                          }}
                        >
                          {t("manageExAuditorsandGroupScreenAuditorName")}
                        </StyledTableCell>
                        <StyledTableCell
                          style={{ width: "45%", textAlign: "left" }}
                        >
                          {t("manageExAuditorsandGroupScreenOrgName")}
                        </StyledTableCell>
                        <StyledTableCell
                          style={{ width: "40%", textAlign: "left" }}
                        >
                          {t("manageExAuditorsandGroupScreenActionsText")}
                        </StyledTableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      <TableRow>
                        <TableCell colSpan={100}>
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "center",
                              alignItems: "center",
                              minHeight: "500px", // Adjust height as needed
                            }}
                          >
                            <p className="text-xl font-bold">
                              {t("manageExAuditorScreenEmptyListText")}
                            </p>
                          </div>
                        </TableCell>
                      </TableRow>
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                <TableContainer sx={{ maxHeight: 450 }}>
                  <Table stickyHeader aria-label="sticky table">
                    <TableHead>
                      <TableRow>
                        {/* <StyledTableCell
                          style={{
                            width: "5%",
                            textAlign: "left",
                            paddingLeft: "20px",
                          }}
                        >
                          Select
                        </StyledTableCell> */}
                        <StyledTableCell
                          style={{
                            paddingLeft: "20px",
                            width: "10%",
                            textAlign: "left",
                          }}
                        >
                          {t("manageExAuditorsandGroupScreenAuditorName")}
                        </StyledTableCell>
                        <StyledTableCell
                          style={{ width: "45%", textAlign: "left" }}
                        >
                          {t("manageExAuditorsandGroupScreenOrgName")}
                        </StyledTableCell>
                        <StyledTableCell
                          style={{ width: "40%", textAlign: "left" }}
                        >
                          {t("manageExAuditorsandGroupScreenActionsText")}
                        </StyledTableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {externalAuditorsData &&
                        externalAuditorsData
                          .slice(
                            page * rowsPerPage,
                            page * rowsPerPage + rowsPerPage
                          )
                          .map((row) => {
                            const isSelected = selectedRows.some(
                              (selectedRow) => {
                                // console.log('Selected User Email:', selectedRow.userEmail); // Log userEmail
                                return selectedRow.userEmail === row.userEmail;
                              }
                            );

                            return (
                              <TableRow
                                hover
                                role="checkbox"
                                tabIndex={-1}
                                key={row.userEmail}
                              >
                                {/* <TableCell
                                  style={{
                                    width: "5%",
                                    textAlign: "left",
                                    paddingLeft: "15px",
                                  }}
                                >
                                  <Checkbox
                                    icon={<RadioButtonUncheckedIcon />}
                                    checkedIcon={<CheckCircleIcon />}
                                    checked={isSelected}
                                    onChange={() => {
                                      if (isSelected) {
                                        // If the row is already selected, deselect it
                                        setSelectedRows([]);
                                      } else {
                                        // If the row is not selected, select it
                                        setSelectedRows([row]);
                                      }
                                    }}
                                    size="small"
                                  />
                                </TableCell> */}
                                <TableCell
                                  style={{
                                    paddingLeft: "20px",
                                    width: "35%",
                                    textAlign: "left",
                                  }}
                                >
                                  <div style={{ wordWrap: "break-word" }}>
                                    {row.name}
                                  </div>
                                </TableCell>
                                <TableCell
                                  style={{ width: "35%", textAlign: "left" }}
                                >
                                  <div style={{ wordWrap: "break-word" }}>
                                    {row.organizationName}
                                  </div>
                                </TableCell>
                                <TableCell
                                  style={{ width: "20%", textAlign: "left" }}
                                >
                                  <div
                                    style={{
                                      display: "flex",
                                      justifyContent: "space-between",
                                      width: "60px",
                                    }}
                                  >
                                    <EditAuditorConfirm
                                      myname={row.name}
                                      myemail={row.userEmail}
                                      myorg={row.organizationName}
                                      onClick={handleRender}
                                    />
                                    <DeleteAuditorConfirm
                                      value={row.userEmail}
                                      onClick={handleRender}
                                    />
                                  </div>
                                </TableCell>
                              </TableRow>
                            );
                          })}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}

              <TablePagination
                rowsPerPageOptions={[10, 25, 100]}
                component="div"
                rowsPerPage={rowsPerPage}
                labelRowsPerPage={t("tablePaginationTitleText")}
                count={externalAuditorsData.length}
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
            </Paper>
          ) : (
            <div
              style={{
                height: "50vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <MuiCircularProgress />
            </div>
          )}
        </div>
      )}

      {table2 && (
        <div>
          <Paper
            sx={{
              width: "100%",
              height: "100%",
              overflow: "hidden",
              borderRadius: "20px",
            }}
          >
            {auditGroupsData.length === 0 ? (
              <TableContainer sx={{ maxHeight: 450 }}>
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      {/* <StyledTableCell
                          style={{
                            width: "5%",
                            textAlign: "left",
                            paddingLeft: "20px",
                          }}
                        >
                          Select
                        </StyledTableCell> */}
                      <StyledTableCell
                        style={{
                          paddingLeft: "20px",
                          width: "10%",
                          textAlign: "left",
                        }}
                      >
                        {t("manageExAuditorsandGroupScreenGroupNameText")}
                      </StyledTableCell>
                      <StyledTableCell
                        style={{ width: "45%", textAlign: "left" }}
                      >
                        {t("manageExAuditorsandGroupScreenNumberOfMembersText")}
                      </StyledTableCell>
                      <StyledTableCell
                        style={{ width: "15%", textAlign: "left" }}
                      >
                        {t("manageExAuditorsandGroupScreenActionsText")}
                      </StyledTableCell>
                    </TableRow>
                  </TableHead>

                  <TableBody>
                    <TableRow>
                      <TableCell colSpan={100}>
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                            minHeight: "500px", // Adjust height as needed
                          }}
                        >
                          <p className="text-xl font-bold">
                            {t("manageExGroupScreenEmptyListText")}
                          </p>
                        </div>
                      </TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </TableContainer>
            ) : (
              <TableContainer sx={{ maxHeight: 450 }}>
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      {/* <StyledTableCell
                          style={{
                            width: "5%",
                            textAlign: "left",
                            paddingLeft: "20px",
                          }}
                        >
                          Select
                        </StyledTableCell> */}
                      <StyledTableCell
                        style={{
                          paddingLeft: "20px",
                          width: "40%",
                          textAlign: "left",
                        }}
                      >
                        {t("manageExAuditorsandGroupScreenGroupNameText")}
                      </StyledTableCell>
                      <StyledTableCell
                        style={{
                          width: "40%",
                          textAlign: "left",
                          paddingLeft: "20px",
                        }}
                      >
                        {t("manageExAuditorsandGroupScreenNumberOfMembersText")}
                      </StyledTableCell>
                      <StyledTableCell
                        style={{ width: "10%", textAlign: "left" }}
                      >
                        {t("manageExAuditorsandGroupScreenActionsText")}
                      </StyledTableCell>
                    </TableRow>
                  </TableHead>

                  <TableBody>
                    {auditGroupsData
                      .slice(
                        page * rowsPerPage,
                        page * rowsPerPage + rowsPerPage
                      )
                      .map((row, index) => {
                        const isSelected = selectedRowsTwo.some(
                          (selectedRow) => {
                            // console.log('Selected User Email:', selectedRow.userEmail); // Log userEmail
                            return (
                              selectedRow.auditGroupName === row.auditGroupName
                            );
                          }
                        );

                        return (
                          <TableRow
                            hover
                            role="checkbox"
                            tabIndex={-1}
                            key={index}
                          >
                            {/* <TableCell
                                style={{
                                  width: "10%",
                                  textAlign: "left",
                                  paddingLeft: "15px",
                                }}
                              >
                                <Checkbox
                                  icon={<RadioButtonUncheckedIcon />}
                                  checkedIcon={<CheckCircleIcon />}
                                  checked={isSelected}
                                  onChange={() => {
                                    if (isSelected) {
                                      // If the row is already selected, deselect it
                                      setSelectedRowsTwo([]);
                                    } else {
                                      // If the row is not selected, select it
                                      setSelectedRowsTwo([row]);
                                    }
                                  }}
                                  size="small"
                                />
                              </TableCell> */}
                            <TableCell
                              style={{
                                paddingLeft: "20px",
                                width: "20%",
                                textAlign: "left",
                              }}
                            >
                              <div style={{ wordWrap: "break-word" }}>
                                {row.auditGroupName}
                              </div>
                            </TableCell>
                            <TableCell
                              style={{ width: "30%", textAlign: "left" }}
                            >
                              <div
                                style={{
                                  paddingLeft: "50px",
                                  paddingRight: "30px",
                                  textAlign: "left",
                                }}
                              >
                                {row.memberCount}
                              </div>
                            </TableCell>
                            <TableCell
                              style={{ width: "20%", textAlign: "left" }}
                            >
                              <div
                                style={{
                                  display: "flex",
                                  justifyContent: "space-between",
                                  width: "60px",
                                }}
                              >
                                <EditGroupConfirm
                                  value={row.auditGroupId}
                                  mygroupname={row.auditGroupName}
                                  mygroupdesc={row.description}
                                  externalAuditorsData={externalAuditorsData}
                                  AuditUsersList={AuditUsersList}
                                  onClick={handleRender}
                                ></EditGroupConfirm>

                                <DeleteAuditGroupConfirm
                                  value={row.auditGroupId}
                                  onClick={handleRender}
                                ></DeleteAuditGroupConfirm>
                              </div>
                            </TableCell>
                          </TableRow>
                        );
                      })}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            <TablePagination
              rowsPerPageOptions={[10, 25, 100]}
              component="div"
              count={auditGroupsData.length}
              // count={Math.ceil(auditGroupsData.length/rowsPerPage)}
              labelRowsPerPage={t("tablePaginationTitleText")}
              rowsPerPage={rowsPerPage}
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
          </Paper>
        </div>
      )}
    </div>
    // </Main>
  );
};

export default AuditorsAndGroups;
