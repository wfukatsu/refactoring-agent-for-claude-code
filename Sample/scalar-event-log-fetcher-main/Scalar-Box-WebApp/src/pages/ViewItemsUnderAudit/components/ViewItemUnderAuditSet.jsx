import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import "./ViewItemUnderAuditSet.css";
import { useParams } from "react-router-dom";
import { CircularProgress, Divider, Paper, TableFooter } from "@mui/material";
import FolderIcon from "@mui/icons-material/Folder";
import DescriptionIcon from "@mui/icons-material/Description";
import DownloadIcon from "@mui/icons-material/Download";
import { blue } from "@mui/material/colors";
import Loader from "../../../common/Loader/Loder";
import Innerunderauditset from "./Innerunderauditset";
import { BASE_URL } from "../../../utils/constants";

import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";

import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import IconButton from "@mui/material/IconButton";
import CloseIcon from "@mui/icons-material/Close";
import Typography from "@mui/material/Typography";
import { SET_LOADING } from "../../../redux/reducerSlice/authSlice";
import ViewAllEventsHistoryByItem from "../../ViewAllEventHistory/ViewAllEventsHistoryByItem";
import DataAndTime from "../../../common/DataAndTime";
import PreviewFile from "./PreviewFile";
import Validating from "../../ExternalAuditorPage/Validating";
import { reset } from "../../../redux/reducerSlice/folderAndFileSlice";
import PathToolTip from "../../../common/FileInfoComponent/PathToolTip";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

const BootstrapDialog = styled(Dialog)(({ theme }) => ({
  width: "100%",
  "& .MuiDialogContent-root": {
    padding: theme.spacing(2),
  },
  "& .MuiDialogActions-root": {
    padding: theme.spacing(1),
  },
}));

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#0061D5",
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const ViewItemUnderAuditSet = () => {
  const [rootFiles, setRootFiles] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const { auditSetId, auditSetName } = useParams();
  const [fileCopies, setFileCopies] = useState(false);
  const [fileCopiesData, setFileCopiesData] = useState([]);
  const [fileVersions, setFileVersions] = useState(false);
  const [fileVersionsData, setFileVersionsData] = useState([]);
  const [eventHistory, setEventHistory] = useState(false);
  const [eventHistoryData, setEventHistoryData] = useState([]);
  const [accessLogs, setAccessLogs] = useState(false);
  const [accessLogsData, setAccessLogsData] = useState([]);
  const [getHashkey, setGetHashKey] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useDispatch();
  const axiosPrivate = useAxiosPrivate();
  const { t, i18n } = useTranslation();

  const handleClickFileCopiesOpen = () => {
    setIsLoading(true);

    axiosPrivate
      .get(
        `${BASE_URL}/box/file/getFileCopies?sha1Hash=${selectedItem.sha1Hash}&itemId=${selectedItem.itemId}&auditSetId=${auditSetId}`
      )
      .then((response) => {
        if (response.data.status) {
          setFileCopiesData(response.data.data);
          setFileCopies(true);
        } else {
          console.error(response.data.message);
        }
      })
      .catch((error) => {
        console.error(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleClickFileVersionOpen = () => {
    setIsLoading(true);
    axiosPrivate
      .get(
        `${BASE_URL}/box/file/getFileVersions?fileId=${selectedItem.itemId}&auditSetId=${auditSetId}`
      )
      .then((response) => {
        if (response.data.status) {
          setFileVersionsData(response.data.data);
          setFileVersions(true);
        } else {
          console.error(response.data.message);
        }
      })
      .catch((error) => {
        console.error(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    console.log("handleChangeRowsPerPage");
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleClickEventHistoryOpen = () => {
    setEventHistory(true);
  };

  const handleClickAccessLogsOpen = () => {
    setIsLoading(true);

    axiosPrivate
      .get(
        `${BASE_URL}/box/auditSet/viewExtAuditorAccessLog?auditSetId=${auditSetId}&itemId=${selectedItem.itemId}`
      )
      .then((response) => {
        if (response.data.status) {
          setAccessLogsData(response.data.data);
          setAccessLogs(true);
        } else {
          console.error(response.data.message);
        }
      })
      .catch((error) => {
        console.error(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleClickAccessLogsClose = () => {
    setAccessLogs(false);
    setPage(0);
  };

  const handleClickEventHistoryClose = () => {
    setEventHistory(false);
    setPage(0);
  };

  const handleClickFileVersionClose = () => {
    setFileVersions(false);
    setPage(0);
  };

  const handleClickFileCopiesClose = () => {
    setPage(0);
    setFileCopies(false);
  };

  const fetchData = (auditSetId) => {
    // if (rootFiles.length !== 0) return;
    if (!isLoaded) {
      dispatch(reset());
      setIsLoading(true);
      axiosPrivate
        .get(
          `${BASE_URL}/box/auditSetItem/viewItemsFromSelectedAuditSet/${auditSetId}`
        )
        .then((response) => {
          if (response.data.status) {
            const data = response.data.data;
            console.log("???????????????????", response.data.data);
            setRootFiles(data);
            if (data.length > 0) {
              setSelectedItem(data[0]);
            }
            setIsLoaded(true);
          } else {
            console.error(response.data.message);
          }
        })
        .catch((error) => {
          setIsLoading(false); // Set loading to false on error
          console.error(error);
        })
        .finally(() => {
          setIsLoading(false); // Set loading to false regardless of success or failure
        });
    }
  };

  //sha1Hash

  // useEffect(() => {
  //   if (selectedItem && selectedItem.itemType === "file") {
  //     handlegetHashkey(); // Call the first API when selectedItem changes and its itemType is 'file'
  //   }
  // }, [handlegetHashkey]);

  useEffect(() => {
    if (auditSetId) {
      fetchData(auditSetId);
    }

    return () => {
      dispatch(reset());
    };
  }, [auditSetId]);

  console.log("????????????????????????", rootFiles.length);

  return (
    <div className="flex flex-col lg:flex-row ">
      {isLoading && (
        <div className="loading-overlay">
          <CircularProgress className="loading-spinner" />
        </div>
      )}
      <div className="flex flex-col border w-full lg:w-3/4 border-blue-500 rounded-xl">
        <div className="bg-blue-500 p-3 text-white font-bold rounded-t-xl">
          {/* <h1>{t("viewItemsUnderAuditScreenSubTitleText")}</h1> */}
          {/* <h1>{t("itemsInText")} {auditSetName}</h1> */}
          {i18n.language === "ja" ? (
            <h1>
              {auditSetName} {t("itemsInText")}
            </h1>
          ) : (
            <h1>
              {t("itemsInText")} {auditSetName}
            </h1>
          )}
        </div>
        <div className="mb-2  ">
          <div className="w-full flex justify-between py-1 border px-5">
            <p className="font-bold px-3.5">
              {t("viewItemsUnderAuditScreenSubNameText")}
            </p>
            <div
              style={{
                display: "flex",
                gap: i18n.language === "ja" ? "6rem" : "4rem",
              }}
            >
              <p className="font-bold">
                {t("viewItemsUnderAuditScreenSubModified")}
              </p>
              <p className="font-bold">
                {t("viewItemsUnderAuditScreenSubSize")}
              </p>
              <p className="font-bold">
                {t("viewItemsUnderAuditScreenSubModifiedBy")}
              </p>
            </div>
          </div>
          {rootFiles.length === 0 && !isLoading ? (
            <div
              style={{
                padding: "30px",
                justifyContent: "center",
                height: "100%",
              }}
            >
              <p
                style={{
                  textAlign: "center",
                }}
              >
                {t("noFilesAvaailbeText")}
              </p>
            </div>
          ) : (
            rootFiles.map((item) => {
              console.log("key ", item.itemId);
              return (
                <div key={item.itemId}>
                  <Innerunderauditset
                    key={item.itemId}
                    item={item}
                    auditSetId={auditSetId}
                    rootId={item.itemId}
                    setSelectedItem={setSelectedItem}
                  />
                </div>
              );
            })
          )}
        </div>
      </div>
      <div style={{ width: "10px" }}></div>
      <Paper
        className="flex flex-col p-0 justify-between border w-full lg:w-1/4 rounded-xl"
        style={{ height: "auto" }}
      >
        {selectedItem && (
          <div className="flex flex-col gap-3 justify-between">
            {selectedItem && (
              <div className="flex flex-col justify-between w-full  ">
                <div>
                  {selectedItem.itemType === "file" && (
                    <PreviewFile selectedItem={selectedItem} />
                  )}
                </div>

                <div className="px-0">
                  <div className="flex justify-between mb-1 py-2 px-1">
                    <h1 className="text-md font-bold gap-2 flex justify-center items-center cursor-pointer">
                      {selectedItem.itemType === "file" ? (
                        <DescriptionIcon sx={{ color: blue[700] }} />
                      ) : (
                        <FolderIcon sx={{ color: blue[700] }} />
                      )}
                      <PathToolTip text={selectedItem.itemName} />
                      {/* {selectedItem.itemName} */}
                    </h1>
                  </div>
                  <Divider variant="fullWidth" color="#fff" />
                  <div className="px-2">
                    <h1 className="text-md font-bold py-2">
                      {t("viewItemsUnderAuditScreenSubFileDetailsText")}
                    </h1>

                    <div className="flex justify-between gap-1">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubCreatedAtText")}{" "}
                      </p>

                      <DataAndTime
                        dataAndTime={selectedItem.createdAt}
                        fontSize="14px"
                      />
                    </div>
                    <div className="flex justify-between gap-1">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubModifiedAtText")}{" "}
                      </p>

                      <DataAndTime
                        dataAndTime={selectedItem.modifiedAt}
                        fontSize="14px"
                      />
                    </div>
                    <div className="flex justify-between gap-1">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubCreatdByText")}{" "}
                      </p>
                      <p className="text-[14px]">{selectedItem.createdBy}</p>
                    </div>
                    <div className="flex justify-between gap-1 items-center">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubModifiedByText")}
                      </p>
                      <p className="text-[14px]">{selectedItem.modifiedBy}</p>
                    </div>
                    <div className="flex justify-between gap-1 items-center">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubSize")}
                      </p>
                      <p className="text-[14px]">{selectedItem.size}</p>
                    </div>
                    {selectedItem.itemType !== "file" ? (
                      <div></div>
                    ) : (
                      <div className="flex justify-between gap-1 items-start">
                        <p className="text-sm font-bold">
                          {t("rightClickMainShaHash")}
                        </p>
                        <div style={{ width: "50%" }}>
                          <p
                            style={{ overflowWrap: "break-word" }}
                            className="text-[14px]"
                          >
                            {selectedItem.sha1Hash}
                          </p>
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            )}

            <Divider
              variant="fullWidth"
              // variant="middle"
              color="#fff"
            />

            {selectedItem.itemType === "file" && (
              <>
                <Validating item={selectedItem} />
                <Divider
                  variant="fullWidth"
                  // variant="middle"
                  color="#fff"
                />
              </>
            )}

            <div className="flex flex-col justify-between gap-2  items-center px-10">
              <button
                onClick={handleClickFileCopiesOpen}
                className={` w-[230px] bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
                  selectedItem?.itemType === "folder"
                    ? "opacity-50 cursor-not-allowed"
                    : ""
                }`}
                style={{ whiteSpace: "nowrap" }}
                disabled={selectedItem?.itemType === "folder"}
              >
                {t("viewItemsUnderAuditScreenSubFileCopiesButtonText")}
              </button>
              <button
                onClick={handleClickFileVersionOpen}
                className={` w-[230px] bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
                  selectedItem?.itemType === "folder"
                    ? "opacity-50 cursor-not-allowed"
                    : ""
                }`}
                disabled={selectedItem?.itemType === "folder"}
              >
                {t("viewItemsUnderAuditScreenSubFileVersionText")}
              </button>
              <button
                onClick={handleClickEventHistoryOpen}
                className={`w-[230px] bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
                  selectedItem?.itemType === "folder"
                    ? "opacity-50 cursor-not-allowed"
                    : ""
                }`}
                disabled={selectedItem?.itemType === "folder"}
              >
                {t("viewItemsUnderAuditScreenSubEventHistoryButtonText")}
              </button>
              <button
                onClick={handleClickAccessLogsOpen}
                className={`w-[230px] bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
                  selectedItem?.itemType === "folder"
                    ? "opacity-50 cursor-not-allowed"
                    : ""
                }`}
                disabled={selectedItem?.itemType === "folder"}
              >
                {t("viewItemsUnderAuditScreenSubAuditAccessLogButton")}
              </button>
              <div style={{ height: "20px" }}></div>
            </div>
          </div>
        )}
      </Paper>

      <BootstrapDialog
        onClose={handleClickFileCopiesClose}
        aria-labelledby="customized-dialog-title"
        open={fileCopies}
        maxWidth="xl"
      >
        <div className="w-[850px] px-3">
          <div className="flex justify-between items-center px-4 ">
            <div>
              {selectedItem && (
                <div style={{ paddingTop: "10px" }}>
                  <p className="text-xl font-bold ">
                    {t("viewItemsUnderAuditScreenSubFileCopiesButtonText")}
                  </p>

                  <p className="text-md ">
                    {t("viewItemsUnderAuditScreenSubFileNameText")} :{" "}
                    {selectedItem.itemName}
                  </p>
                </div>
              )}
            </div>
            <IconButton aria-label="close" onClick={handleClickFileCopiesClose}>
              <CloseIcon />
            </IconButton>
          </div>
          <DialogContent dividers>
            <>
              <Paper
                style={{
                  overflow: "hidden",
                  // borderRadius: "25px",
                  height: "500px",
                }}
              >
                <TableContainer style={{ maxHeight: 440 }}>
                  <Table stickyHeader aria-label="sticky table">
                    <TableHead>
                      <TableRow>
                        <StyledTableCell>
                          {t("viewItemsUnderAuditScreenSubFileNameText")}
                        </StyledTableCell>
                        <StyledTableCell>
                          {t("viewItemsUnderAuditScreenSubCreatedAtText")}
                        </StyledTableCell>
                        <StyledTableCell>
                          {t("viewItemsUnderAuditScreenSubPathText")}
                        </StyledTableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {Array.isArray(fileCopiesData) &&
                      fileCopiesData.length > 0 ? (
                        <>
                          {(rowsPerPage > 0
                            ? fileCopiesData.slice(
                                page * rowsPerPage,
                                page * rowsPerPage + rowsPerPage
                              )
                            : fileCopiesData
                          ).map((fileCopy, index) => (
                            <TableRow key={index}>
                              <TableCell>
                                {fileCopy.itemName}
                                {fileCopy.isDeleted && (
                                  <span
                                    style={{
                                      marginLeft: "10px",
                                      borderRadius: "5px",
                                      paddingTop: "2px",
                                      paddingBottom: "2px",
                                      paddingRight: "5px",
                                      paddingLeft: "5px",
                                      backgroundColor: "#E0E0E0",
                                      color: "black",
                                      whiteSpace: "nowrap",
                                    }}
                                  >
                                    {t("deletedText")}
                                  </span>
                                )}
                              </TableCell>

                              <TableCell>
                                <DataAndTime
                                  dataAndTime={fileCopy.createdAt}
                                  fontSize="14px"
                                />
                              </TableCell>
                              <TableCell>{fileCopy.path}</TableCell>
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
                                minHeight: "440px", // Adjust height as needed
                              }}
                            >
                              <p className="text-xl font-bold">
                                {t(
                                  "viewItemsUnderAuditScreenSubFileCopiesDailogErrorMessageText"
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
                  <TableRow>
                    <TablePagination
                      rowsPerPageOptions={[10, 25, 100]}
                      component="div"
                      rowsPerPage={rowsPerPage}
                      count={fileCopiesData.length}
                      labelRowsPerPage={t("tablePaginationTitleText")}
                      page={page}
                      onPageChange={(event, pageNumber) => {
                        handleChangePage(event, pageNumber);
                      }}
                      onChangeRowsPerPage={handleChangeRowsPerPage}
                      labelDisplayedRows={({ from, to, count }) => {
                        if (i18n.language == "ja") {
                          return `全アイテム  ${count} ${t(
                            "tablePageOffText"
                          )} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(
                            count / rowsPerPage
                          )})`;
                        }

                        return `${from}-${to} ${t(
                          "tablePageOffText"
                        )} ${count} (${Math.ceil(count / rowsPerPage)} ${t(
                          "tablePageText"
                        )})`;
                      }}
                      style={{
                        display: "flex",
                        justifyContent: "flex-end",
                      }} // Align pagination to the right
                    />
                  </TableRow>
                </TableFooter>
              </Paper>
            </>
          </DialogContent>
        </div>
      </BootstrapDialog>

      <BootstrapDialog
        onClose={handleClickFileVersionClose}
        aria-labelledby="customized-dialog-title"
        open={fileVersions}
        maxWidth="xl"
      >
        <div className="w-[750px] px-3">
          <div className="flex justify-between items-center px-4">
            {selectedItem && (
              <div style={{ paddingTop: "10px" }}>
                <p className="text-xl font-bold ">
                  {t("viewItemsUnderAuditScreenSubFileVersionText")}
                </p>
                <p className="text-md ">
                  {t("viewItemsUnderAuditScreenSubFileNameText")} :{" "}
                  {selectedItem.itemName}
                </p>
              </div>
            )}
            <IconButton
              aria-label="close"
              onClick={handleClickFileVersionClose}
            >
              <CloseIcon />
            </IconButton>
          </div>
          <DialogContent dividers>
            <Paper
              style={{
                overflow: "hidden",
                // borderRadius: "25px",
                height: "500px",
              }}
            >
              <TableContainer style={{ maxHeight: 440 }}>
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      <StyledTableCell>
                        {t("viewItemsUnderAuditScreenSubVersionText")}
                      </StyledTableCell>
                      <StyledTableCell>
                        {t("viewItemsUnderAuditScreenSubUpdatedAtText")}
                      </StyledTableCell>
                      <StyledTableCell>
                        {t("viewItemsUnderAuditScreenSubHashText")}
                      </StyledTableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {fileVersionsData && fileVersionsData.length > 0 ? (
                      <>
                        {(rowsPerPage > 0
                          ? fileVersionsData.slice(
                              page * rowsPerPage,
                              page * rowsPerPage + rowsPerPage
                            )
                          : fileVersionsData
                        ).map((row, index) => (
                          <TableRow key={index}>
                            <TableCell>V{row.itemVersionNumber}</TableCell>
                            <TableCell>
                              <DataAndTime
                                dataAndTime={row.modifiedAt}
                                fontSize="14px"
                              />
                            </TableCell>
                            <TableCell>{row.sha1Hash}</TableCell>
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
                              minHeight: "440px", // Adjust height as needed
                            }}
                          >
                            <p className="text-xl font-bold">
                              {t(
                                "viewItemsUnderAuditScreenSubFileVersionDailogErrorMessageText"
                              )}
                            </p>
                          </div>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
              <TableFooter sx={{ display: "flex", justifyContent: "flex-end" }}>
                <TableRow>
                  <TablePagination
                    rowsPerPageOptions={[10, 25, 100]}
                    component="div"
                    rowsPerPage={rowsPerPage}
                    count={fileVersionsData.length}
                    labelRowsPerPage={t("tablePaginationTitleText")}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                    labelDisplayedRows={({ from, to, count }) => {
                      if (i18n.language == "ja") {
                        return `全アイテム  ${count} ${t(
                          "tablePageOffText"
                        )} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(
                          count / rowsPerPage
                        )})`;
                      }

                      return `${from}-${to} ${t(
                        "tablePageOffText"
                      )} ${count} (${Math.ceil(count / rowsPerPage)} ${t(
                        "tablePageText"
                      )})`;
                    }}
                  />
                </TableRow>
              </TableFooter>
            </Paper>
          </DialogContent>
        </div>
      </BootstrapDialog>

      <BootstrapDialog
        onClose={handleClickEventHistoryClose}
        aria-labelledby="customized-dialog-title"
        open={eventHistory}
        maxWidth="xl"
      >
        <div style={{ height: "650px", width: "1080px", padding: "10px" }}>
          {selectedItem !== null &&
          typeof selectedItem === "object" &&
          "itemId" in selectedItem ? (
            // Render your component with the valid selectedItem
            <div>
              <div className="flex justify-between items-center">
                <div>
                  <p className="text-xl font-bold ">
                    {" "}
                    {t("viewItemsUnderAuditScreenSubEventHistoryButtonText")}
                  </p>
                  <p className="text-md ">
                    {t("viewItemsUnderAuditScreenSubFileNameText")} :{" "}
                    {selectedItem.itemName}
                  </p>
                </div>
                <IconButton
                  aria-label="close"
                  onClick={handleClickEventHistoryClose}
                >
                  <CloseIcon />
                </IconButton>
              </div>
              <ViewAllEventsHistoryByItem
                auditsetId={auditSetId}
                FileItemId={selectedItem.itemId}
                FileType={selectedItem.type}
              />
            </div>
          ) : (
            <p>
              {t(
                "viewItemsUnderAuditScreenSubEventHistoryDailogErrorMessageText"
              )}
            </p>
          )}
        </div>
      </BootstrapDialog>

      <BootstrapDialog
        onClose={handleClickAccessLogsClose}
        aria-labelledby="customized-dialog-title"
        open={accessLogs}
        maxWidth="xl"
      >
        <div className="w-[750px] px-3">
          <div className="flex justify-between items-center px-4">
            {selectedItem && (
              <div style={{ paddingTop: "10px" }}>
                <p className="text-xl font-bold ">
                  {t("viewItemsUnderAuditScreenSubAuditAccessLogButton")}
                </p>
                <p className="text-md ">
                  {t("viewItemsUnderAuditScreenSubFileNameText")} :{" "}
                  {selectedItem.itemName}
                </p>
              </div>
            )}
            <IconButton aria-label="close" onClick={handleClickAccessLogsClose}>
              <CloseIcon />
            </IconButton>
          </div>
          <DialogContent dividers>
            <Paper
              style={{
                overflow: "hidden",
                // borderRadius: "25px",
                height: "500px",
              }}
            >
              <TableContainer style={{ maxHeight: 440 }}>
                <Table stickyHeader aria-label="sticky table">
                  <TableHead>
                    <TableRow>
                      <StyledTableCell>
                        {t("viewItemsUnderAuditScreenSubOperationsText")}
                      </StyledTableCell>
                      <StyledTableCell>
                        {t("viewItemsUnderAuditScreenSubPerformedByText")}
                      </StyledTableCell>
                      <StyledTableCell>
                        {t("viewItemsUnderAuditScreenSubPerformedAt")}
                      </StyledTableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {Array.isArray(accessLogsData) &&
                    accessLogsData.length > 0 ? (
                      <>
                        {(rowsPerPage > 0
                          ? accessLogsData.slice(
                              page * rowsPerPage,
                              page * rowsPerPage + rowsPerPage
                            )
                          : accessLogsData
                        ).map((log, index) => (
                          <TableRow key={index}>
                            <TableCell>{log.eventType}</TableCell>
                            <TableCell>{log.ownerName}</TableCell>
                            <TableCell>
                              <DataAndTime
                                dataAndTime={log.eventDate}
                                fontSize="14px"
                              />
                            </TableCell>
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
                              minHeight: "440px", // Adjust height as needed
                            }}
                          >
                            <p className="text-xl font-bold">
                              {t(
                                "viewItemsUnderAuditScreenSubAuditAccessLogsDailogErrorMessage"
                              )}
                            </p>
                          </div>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
              <TableFooter sx={{ display: "flex", justifyContent: "flex-end" }}>
                <TableRow>
                  <TablePagination
                    rowsPerPageOptions={[10, 25, 100]}
                    component="div"
                    rowsPerPage={rowsPerPage}
                    count={accessLogsData.length}
                    labelRowsPerPage={t("tablePaginationTitleText")}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                    labelDisplayedRows={({ from, to, count }) => {
                      if (i18n.language == "ja") {
                        return `全アイテム  ${count} ${t(
                          "tablePageOffText"
                        )} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(
                          count / rowsPerPage
                        )})`;
                      }

                      return `${from}-${to} ${t(
                        "tablePageOffText"
                      )} ${count} (${Math.ceil(count / rowsPerPage)} ${t(
                        "tablePageText"
                      )})`;
                    }}
                  />
                </TableRow>
              </TableFooter>
            </Paper>
          </DialogContent>
        </div>
      </BootstrapDialog>
    </div>
  );
};

export default ViewItemUnderAuditSet;
