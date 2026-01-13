import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { Button, Checkbox, TablePagination } from "@mui/material";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import { styled } from "@mui/material/styles";
import CustomizedDialogs from "./components/AuditDailogBox";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import { useNavigate } from "react-router-dom";
import DeletePopUp from "../../common/DeletePopUp/DeletePopUp";
import ErrorPopUp from "../../common/ErrorPopUp/ErrorPopUp";
import UserSettingDailog from "./components/UserSettingDailogBox";
import { BASE_URL } from "../../utils/constants";
import LoadingButton from "@mui/lab/LoadingButton";

// import LoadingButton from "@mui/lab/LoadingButton";

import SuccessPopUp from "../../common/SuccessPopUp/SuccessPopUp";

import Tooltip, { tooltipClasses } from "@mui/material/Tooltip";
import DataAndTime from "../../common/DataAndTime";

import MuiCircularProgress from "@mui/joy/CircularProgress";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

const LightTooltip = styled(({ className, ...props }) => (
  <Tooltip {...props} classes={{ popper: className }} />
))(({ theme }) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: theme.palette.common.white,
    color: "rgba(0, 0, 0, 0.87)",
    boxShadow: theme.shadows[1],
    fontSize: 11,
  },
}));

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#0061D5",
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 10,
  },
}));
const AuditSet = ({ open }) => {
  const [respoData, setResData] = useState([]);
  const [selectedRows, setSelectedRows] = useState([]);
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);
  const [deleteConfirmPopup, setDeleteConfimPopup] = React.useState(false);
  const [showErrorPopup, setShowErrorPopup] = React.useState(false);
  const [deleteAuditId, setDeleteAuditId] = React.useState(null);
  const [isLoadingViewItems, setIsLoadingViewItems] = React.useState(false);
  const navigate = useNavigate();

  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [messagePopUp, setMessagePopUp] = React.useState("");

  const [loading, setLoading] = useState(false);
  const axiosPrivate = useAxiosPrivate();

  const {t,i18n} = useTranslation();

  const handleClickOpenFolderFile = () => {
    if (selectedRows.length === 0) {
      console.log("No row selected.");
      return;
    }

    const auditSetId = selectedRows[0].auditSetId;
    const auditSetName = selectedRows[0].auditSetName;
    navigate(`/auditset/viewfolderandfiles/${auditSetId}/${auditSetName}`);
    return;
  };
  useEffect(() => {
    getManageAudit();
  }, []);

  const getManageAudit = () => {
    const url = `${BASE_URL}/box/auditSet/getMyAuditSetList`;
    
    axiosPrivate
      .get(url)
      .then((response) => {
        setResData(response.data.data);
        setSelectedRows([]);
        setLoading(true);
      })
      .catch((error) => {
        setResData([]);
        console.error(error);
      });
  };

  const deleteAudit = (selectedId) => {
    const url = `${BASE_URL}/box/auditSet/deleteAuditSet/${selectedId}`;
    
    axiosPrivate
      .delete(url)
      .then((response) => {
        getManageAudit();
        setMessagePopUp(response.data.message);
        setShowSuccessPopup(true);
      })
      .catch((error) => {
        if (error.response && error.response.status === 400) {
          setShowErrorPopup(true);
        } else {
          setShowErrorPopup(true);
        }
        setMessagePopUp(error.response.data.message);
      });
  };
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };
  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  return (
    //MAIN
    <div>
      <div className="flex flex-col gap-2">
        <div className="flex justify-between">
          <h1 className="font-bold text-xl">{t("manageAuditSetScreenTitleText")}</h1>
          <div className="flex">
            <CustomizedDialogs getListofAudit={getManageAudit} />
            <div style={{ width: "10px", height: "10px" }}></div>

            <button
            className={`bg-[#1976d2] hover:bg-[#1976d2] text-white  py-2 px-4 rounded-full ${
              selectedRows.length === 0  ? "opacity-50 cursor-not-allowed" : ""
            }`}
              style={{ borderRadius: 28, textTransform: "none" ,backgroundColor:"#1976d2",}}
              size="small"
              onClick={handleClickOpenFolderFile}
              // disabled={selectedRows.length === 0}
              // loading={isLoadingViewItems}
              
              // variant="contained"
            >
              <span>{t("manageAuditSetScreenViewItemsButtonText")}</span>
            </button>
          </div>
        </div>
        {loading ? (
          <div>
            <Paper
              sx={{ width: "100%", overflow: "hidden", borderRadius: "20px" }}
            >
              <TableContainer sx={{ maxHeight: 500 }}>
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      <StyledTableCell
                        style={{ paddingLeft: "20px", width: "10%" }}
                      >
                        {t("manageAuditSetScreenSelectText")}
                      </StyledTableCell>
                      <StyledTableCell>{t("manageAuditSetScreenAuditSetNameText")}</StyledTableCell>
                      <StyledTableCell>{t("manageAuditSetScreenCreatedOnText")}</StyledTableCell>
                      <StyledTableCell>{t("manageAuditSetScreenOwnerText")}</StyledTableCell>
                      <StyledTableCell>{t("manageAuditSetScreenActions")}</StyledTableCell>
                    </TableRow>
                  </TableHead>
                  {respoData && respoData.length === 0 ? (
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
                      {respoData &&
                        respoData
                          .slice(
                            page * rowsPerPage,
                            page * rowsPerPage + rowsPerPage
                          )
                          .map((row) => {
                            const isSelected = selectedRows.some(
                              (item) => item.auditSetId === row.auditSetId
                            );
                            return (
                              <TableRow
                                hover
                                role="checkbox"
                                tabIndex={-1}
                                key={row.auditSetId}
                                selected={isSelected}
                              >
                                <TableCell>
                                  <Checkbox
                                    icon={<RadioButtonUncheckedIcon />}
                                    checkedIcon={<CheckCircleIcon />}
                                    checked={isSelected}
                                    // onChange={() => setSelectedRows([row])
                                    // }
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
                                </TableCell>
                                <TableCell>
                                  <div
                                    style={{
                                      wordWrap: "break-word",
                                    }}
                                  >
                                    {row.auditSetName}
                                  </div>
                                </TableCell>
                                <TableCell>
                                  {/* {convertTimestampToString(row.createdAt)} */}
                                  <DataAndTime dataAndTime={row.createdAt} />
                                </TableCell>
                                <TableCell>{row.ownedBy}</TableCell>
                                <TableCell>
                                  <div
                                    style={{
                                      display: "flex",
                                      justifyContent: "space-between",
                                      width: "100px",
                                    }}
                                  >
                                    <CustomizedDialogs
                                      selectedAuditId={row.auditSetId}
                                      selectedAuditName={row.auditSetName}
                                      selectedOwnerEmail={row.ownedBy}
                                      selectedAuditDescrption={row.description}
                                      getListofAudit={getManageAudit}
                                    />
                                    <Tooltip title={t("manageAuditSetScreenDeleteDailogTitleMessageText")} arrow>
                                      <DeleteIcon
                                        style={{ cursor: "pointer" }}
                                        onClick={() => {
                                          console.log(
                                            "delete ",
                                            row.auditSetId
                                          );
                                          setDeleteAuditId(row.auditSetId);
                                          setDeleteConfimPopup(true);
                                        }}
                                      />
                                    </Tooltip>
                                    <UserSettingDailog
                                      selectedAuditId={row.auditSetId}
                                      selectedEmail={row.ownedBy}
                                      getListofAudit={getManageAudit}
                                      style={{ marginLeft: "auto" }}
                                    />
                                  </div>
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
                count={respoData.length}
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
            </Paper>
          </div>
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
      {deleteConfirmPopup && (
        <DeletePopUp
          iconColor="primary"
          iconSize={70}
          title={t("manageAuditSetScreenDeleteDailogTitleMessageText")}
          open={deleteConfirmPopup}
          handleClose={() => setDeleteConfimPopup(false)}
          handleDelete={() => {
            if (deleteAuditId !== null) {
              console.log("id availabe", deleteAuditId);
              deleteAudit(deleteAuditId);
            } else {
              console.log("audit id");
            }
          }}
        />
      )}
      {showSuccessPopup && (
        <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={messagePopUp}
          open={showSuccessPopup}
          handleClose={() => {
            setDeleteConfimPopup(false);
            setShowSuccessPopup(false);
          }}
        />
      )}
      {showErrorPopup && (
        <ErrorPopUp
          iconColor="error"
          iconSize={50}
          title={messagePopUp}
          open={showErrorPopup}
          handleClose={() => setShowErrorPopup(false)}
        />
      )}
    </div>
  );
};
export default AuditSet;
