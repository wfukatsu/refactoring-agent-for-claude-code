import React, { useEffect, useState } from "react";
import {
  Box,
  Checkbox,
  CircularProgress,
  Dialog,
  IconButton,
  TableFooter,
  Tooltip,
} from "@mui/material";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import { styled } from "@mui/material/styles";
import { useDispatch, useSelector } from "react-redux";
import DescriptionIcon from "@mui/icons-material/Description";
import { BASE_URL } from "../../utils/constants";
import { useNavigate } from "react-router-dom";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CloseIcon from "../../assets/CloseIconSvg";

import { reset } from "../../redux/reducerSlice/folderAndFileSlice";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import { useTranslation } from "react-i18next";



const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#0061D5",
    color: theme.palette.common.white,
    textAlign: "left",
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
    textAlign: "left",
  },
}));

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  width: "100%",
  "& .MuiDialogContent-root": {
    padding: theme.spacing(2),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
  },
}));

const ViewitemsUnderAudit = () => {
  const [externalAuditorsData, setExternalAuditorsData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const navigate = useNavigate();
  const [selectedRow, setSelectedRow] = useState(null);
  const [validate, setValidate] = useState(null);
  const dispatch = useDispatch();

  const axiosPrivate = useAxiosPrivate();
  const { t, i18n } = useTranslation();

  const [isLoading, setIsloading] = useState(false);

  const [loading, setLoading] = useState(false);

  // const data = [
  //   { fileId: 1, name: "sample.txt", path: "/path/xyz.txt" },
  //   { fileId: 2, name: "sample.txt", path: "/path/xyz.txt" },
  //   { fileId: 3, name: "sample.txt", path: "/path/xyz.txt" },
  // ];

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  // const handleClickOpen = (rowData) => {
  //   setSelectedRowData(rowData);
  //   setOpenDilogBox(true);
  //   setPersonName(rowData.roleJson);
  //   // }
  // };

  const fetchAuditsetValidate = async () => {
    setIsloading(true);
    try {
      const response = await axiosPrivate.get(
        `${BASE_URL}/box/auditSet/validateAuditSet/${selectedRow.auditSetId}`
      );
      if (response.status === 200) {
        setValidate(response.data.data);
      } else {
        console.error("Failed to fetch user data:", response.statusText);
      }
    } catch (error) {
      console.error("Error fetching user data:", error);
    } finally {
      setIsloading(false);
    }
  };

  const handleValidate = () => {
    if (selectedRow) {
      fetchAuditsetValidate();
    } else {
      console.log("No row selected!");
    }
  };

  const handleClickPopupClose = () => {
    setValidate(null);
    setSelectedRow(null);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  useEffect(() => {
    getManageAudit();
    return () => {
      dispatch(reset());
    };
  }, []);

  const getManageAudit = () => {
    setLoading(true);
    const url = `${BASE_URL}/box/auditSet/getMyAuditSetList`;
    axiosPrivate
      .get(url)
      .then((response) => {
        setExternalAuditorsData(response.data.data);
      })
      .catch((error) => {
        console.error(error);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleDescriptionIconClick = (auditSetId,auditSetName) => {
    navigate(`/viewitemunderaudit/viewitemunderauditset/${auditSetId}/${auditSetName}`);
  };

  return (
    <div className="flex flex-col gap-2">
      {isLoading && (
        <div className="loading-overlay">
          <CircularProgress className="loading-spinner" />
        </div>
      )}
      <>
        <div className="flex justify-between">
          <h1 className="font-bold text-xl">
            {t("viewItemsUnderAuditScreenTitleText")}
          </h1>
          {/* <ValidateButton selectedItem={selectedRow} setValidate={setValidate} /> */}

          <button
            className={`bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
              selectedRow === null ? "opacity-50 cursor-not-allowed" : ""
            }`}
            disabled={selectedRow === null}
            onClick={() => {
              if (selectedRow !== null) {
                handleValidate(selectedRow);
              }
            }}
          >
            {t("viewItemsUnderAuditScreenValidateButtonText")}
          </button>
        </div>
        <div>
         { loading ?
          <div
            style={{
              height: "50vh",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <CircularProgress />
          </div>
         :
         <Paper
            sx={{ width: "100%", overflow: "hidden", borderRadius: "20px" }}
          >
            <TableContainer sx={{ maxHeight: 550 }}>
              <Table stickyHeader aria-label="sticky table">
                <TableHead>
                  <TableRow>
                    <StyledTableCell style={{ width: "5%" }}>
                      {t("viewItemsUnderAuditScreenSelectText")}
                    </StyledTableCell>
                    <StyledTableCell>
                      {t("viewItemsUnderAuditScreenAuditSetName")}
                    </StyledTableCell>
                    <StyledTableCell>
                      {t("viewItemsUnderAuditScreenAuditDescription")}
                    </StyledTableCell>
                    <StyledTableCell
                      style={{ width: "15%", textAlign: "center" }}
                    >
                      {t("viewItemsUnderAuditScreenActionsText")}
                    </StyledTableCell>
                  </TableRow>
                </TableHead>
                {externalAuditorsData && externalAuditorsData.length === 0 ? (
                  <TableBody>
                    <TableRow>
                      <TableCell colSpan={5}>
                        <div
                          style={{
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                            minHeight: "400px",
                          }}
                        >
                          <p className="text-xl font-bold">
                            {t("manageAuditSetEmptyListText")}
                          </p>
                        </div>
                      </TableCell>
                    </TableRow>
                  </TableBody>
                ) : (
                  <TableBody>
                    {externalAuditorsData &&
                      externalAuditorsData
                        .slice(
                          page * rowsPerPage,
                          page * rowsPerPage + rowsPerPage
                        )
                        .map((row) => {
                          return (
                            <TableRow
                              hover
                              role="checkbox"
                              tabIndex={-1}
                              key={row.createdAt}
                            >
                              <TableCell>
                                <Checkbox
                                  icon={<RadioButtonUncheckedIcon />}
                                  checkedIcon={<CheckCircleIcon />}
                                  checked={
                                    selectedRow?.auditSetId === row.auditSetId
                                  }
                                  onChange={() => {
                                    if (
                                      selectedRow &&
                                      selectedRow?.auditSetId === row.auditSetId
                                    ) {
                                      setSelectedRow(null);
                                    } else {
                                      setSelectedRow(row);
                                    }
                                  }}
                                  size="small"
                                />
                              </TableCell>
                              <TableCell>
                                <div style={{ wordWrap: "break-word" }}>
                                  {row.auditSetName}
                                </div>
                              </TableCell>
                              <TableCell>
                                <div style={{ wordWrap: "break-word" }}>
                                  {row.description}
                                </div>
                              </TableCell>
                              <TableCell style={{ textAlign: "center" }}>
                                <Tooltip title="View Items" arrow>
                                  <IconButton
                                    onClick={() =>
                                      handleDescriptionIconClick(row.auditSetId,row.auditSetName)
                                    }
                                  >
                                    <DescriptionIcon />
                                  </IconButton>
                                </Tooltip>
                              </TableCell>
                            </TableRow>
                          );
                        })}
                  </TableBody>
                )}
              </Table>
            </TableContainer>

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
          </Paper>}
        </div>

        <BootstrapDialog
          onClose={handleClickPopupClose}
          aria-labelledby="customized-dialog-title"
          open={validate !== null && selectedRow !== null}
          maxWidth="md"
        >
          <div className="w-[750px] px-2">
            <div className="flex justify-between items-center py-1 px-1 ">
              {selectedRow && (
                <div>
                  <div className="flex justify-between  items-center py-1 w-[720px]">
                    <p className="text-2xl font-bold ">
                      {t("viewItemsUnderAuditScreenVDailogTitleText")}
                    </p>

                    <IconButton
                      aria-label="close"
                      onClick={handleClickPopupClose}
                    >
                      <CloseIcon />
                    </IconButton>
                  </div>
                  <p style={{fontSize:18,fontWeight:"bold",display:"flex"}}>
                    {t("viewItemsUnderAuditScreenVDailogAuditNameText")}
                    <span style={{ fontSize: "18px",marginLeft: 5, fontWeight:"normal" }}>
                      {`${selectedRow.auditSetName}`}
                    </span>
                  </p>

                  {validate && (
                    <>
                      <div className="flex justify-between  items-center py-1 w-[720px]">
                        <p className="text-l font-bold ">
                          {t(
                            "viewItemsUnderAuditScreenVDailogTotalFilesCheckedText"
                          )}
                          <span style={{fontWeight:"normal"}}> {validate.totalFilesChecked}</span>
                        </p>
                        <p className="text-l font-bold ">
                          {t(
                            "viewItemsUnderAuditScreenVDailogFilesTamperedText"
                          )}
                          <span style={{fontWeight:"normal"}}> {validate.filesTamperedCount}</span>
                        </p>
                      </div>
                    </>
                  )}
                </div>
              )}
            </div>
            <div>
              <Paper
                style={{
                  overflow: "hidden",
                  
                }}
              >
                <TableContainer style={{ height: 350 }}>
                  <Table stickyHeader aria-label="sticky table">
                    <TableHead>
                      <TableRow>
                        <StyledTableCell sx={{ width: "40%" }}>
                          {t("viewItemsUnderAuditScreenVDailogTableNameText")}
                        </StyledTableCell>

                        <StyledTableCell>
                          {t("viewItemsUnderAuditScreenVDailogTablePathText")}
                        </StyledTableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {validate &&
                      Array.isArray(validate.tamperedFiles) &&
                      validate.tamperedFiles.length > 0 ? (
                        <>
                          {(rowsPerPage > 0
                            ? validate.tamperedFiles.slice(
                                page * rowsPerPage,
                                page * rowsPerPage + rowsPerPage
                              )
                            : validate.tamperedFiles
                          ).map((row, index) => (
                            <TableRow key={index}>
                              <TableCell>{row.name}</TableCell>

                              <TableCell>{row.path}</TableCell>
                            </TableRow>
                          ))}
                        </>
                      ) : (
                        <TableRow>
                          <TableCell colSpan={4} align="center">
                            <div
                              style={{
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                                minHeight: "260px", // Adjust height as needed
                              }}
                            >
                              <p className="text-xl font-bold">
                                {t(
                                  "viewItemsUnderAuditScreenVDailogTableEmptyListMessageText"
                                )}
                              </p>
                            </div>
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
                <TableFooter
                  sx={{ display: "flex", justifyContent: "flex-end" }}
                >
                  {validate && (
                    <TableRow>
                      <TablePagination
                        rowsPerPageOptions={[10, 25, 100]}
                        component="div"
                        rowsPerPage={rowsPerPage}
                        count={validate.tamperedFiles.length}
                        labelRowsPerPage={t("tablePaginationTitleText")}
                        page={page}
                        onPageChange={handleChangePage}
                        onChangeRowsPerPage={handleChangeRowsPerPage}
                        labelDisplayedRows={({ from, to, count }) => {

                          if(i18n.language == "ja"){
                            return `全アイテム  ${count} ${t("tablePageOffText")} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(count / rowsPerPage)})`;
                          }
      
                          return `${from}-${to} ${t("tablePageOffText")} ${count} (${Math.ceil(
                            count / rowsPerPage
                          )} ${t("tablePageText")})`;

                        }}
                        style={{
                          display: "flex",
                          justifyContent: "flex-end",
                        }}
                      />
                    </TableRow>
                  )}
                </TableFooter>
              </Paper>
              <div style={{height:"20px"}}></div>
            </div>
          </div>
        </BootstrapDialog>
      </>
    </div>
  );
};

export default ViewitemsUnderAudit;
