import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import "../ViewItemsUnderAudit/components/ViewItemUnderAuditSet.css";
import { useParams } from "react-router-dom";
import { CircularProgress, Divider, Paper, TableFooter } from "@mui/material";
import DescriptionIcon from "@mui/icons-material/Description";
import FolderIcon from "@mui/icons-material/Folder";

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

import {
  BASE_URL,
  EVENT_HISTORY,
  FILE_COPIES,
  VERSION_HISTORY,
  convertUtcToLocal,
} from "../../utils/constants";
import Header from "../../common/Header/Header";
import Node from "../AuditSet/components/Node";
import ExternalPreviewFile from "./ExternalPreviewFile";
import { blue } from "@mui/material/colors";
import OpenPropertiesDialog from "./OpenPropertiesDialog";
import Validating from "./Validating";
import DataAndTime from "../../common/DataAndTime";

import { reset } from "../../redux/reducerSlice/folderAndFileSlice";

import ViewAllEventsHistoryByItem from "../ViewAllEventHistory/ViewAllEventsHistoryByItem";
import PathToolTip from "../../common/FileInfoComponent/PathToolTip";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

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

const ExternalAuditorViewitemAnderAuditSet = () => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [rootFiles, setRootFiles] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const { auditSetId, auditSetName } = useParams();
  const [isLoading, setIsLoading] = useState(false);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [fileVersions, setFileVersions] = useState(false);
  const [fileVersionsData, setFileVersionsData] = useState([]);

  const [fileCopies, setFileCopies] = useState(false);
  const [fileCopiesData, setFileCopiesData] = useState([]);

  const [eventHistory, setEventHistory] = useState(false);
  const [eventHistoryData, setEventHistoryData] = useState([]);
  const axiosPrivate = useAxiosPrivate();
  const { t, i18n } = useTranslation();
  // const user = useSelector((state) => state.auth.user);

  const dispatch = useDispatch();

  const fetchData = (auditSetId) => {
    if (!isLoaded) {
      setIsLoading(true);
      axiosPrivate
        .get(
          `${BASE_URL}/box/auditSetItem/viewItemsFromSelectedAuditSet/${auditSetId}`
        )
        .then((response) => {
          if (response.data.status) {
            console.log("???????????????????", response.data.data);
            const data = response.data.data;
            setRootFiles(data);
            if (data.length > 0) {
              setSelectedItem(data[0]);
            }
            setIsLoaded(false);
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

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleClickEventHistoryClose = () => {
    setEventHistory(false);
    setPage(0);
  };

  const handleClickFileCopiesClose = () => {
    setFileCopies(false);
    setPage(0);
  };

  const handleClickFileVersionClose = () => {
    setFileVersions(false);
    setPage(0);
  };

  const fetchFileCopies = (hashkey, itemId) => {
    console.log("hashkey :: ", hashkey);
    setIsLoading(true);
    axiosPrivate
      .get(
        `${BASE_URL}/box/file/getFileCopies?sha1Hash=${hashkey}&itemId=${itemId}&auditSetId=${auditSetId}`
      )
      .then((response) => {
        if (response.data.status) {
          console.log("RESPONSE :: ", response);
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

  const handleClickFileCopiesOpen = () => {
    fetchFileCopies(selectedItem.sha1Hash, selectedItem.itemId);
  };

  const handleClickEventHistoryOpen = () => {
    if (selectedItem.itemType === "folder") return;
    setIsLoading(true);
    axiosPrivate
      .get(`${BASE_URL}/box/file/getFileVersions?fileId=${selectedItem.itemId}`)
      .then((response) => {
        console.log("RESPONSE :: ", response);
        if (response.data.status) {
          setFileVersionsData(response.data.data);
          setFileVersions(true);
        } else {
          console.error(response.data.message);
        }
      })
      .catch((error) => {
        console.error("ERROR :: ", error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleClickVersionHistoryOpen = () => {
    console.log("??????????", selectedItem);
    if (selectedItem.itemType === "folder") return;
    axiosPrivate
      .get(`${BASE_URL}/box/file/getFileVersions?fileId=${selectedItem.itemId}`)
      .then((response) => {
        console.log("RESPONSE :: ", response);
        if (response.data.status) {
          setEventHistoryData(response.data.data);
          setEventHistory(true);
        } else {
          console.error(response.data.message);
        }
      })
      .catch((error) => {
        console.error("ERROR :: ", error);
      });
  };

  const handleSelectedOption = (selectedOption) => {
    console.log("VALUE VALUE", selectedOption);

    if (selectedOption === VERSION_HISTORY) {
      // handleClickVersionHistoryOpen();
      handleClickEventHistoryOpen();
    } else if (selectedOption === FILE_COPIES) {
      handleClickFileCopiesOpen();
    } else if (selectedOption === EVENT_HISTORY) {
      setEventHistory(true);
    }
  };

  useEffect(() => {
    if (auditSetId) {
      // dispatch(reset());
      setRootFiles([]);
      fetchData(auditSetId);
    }

    return () => {
      dispatch(reset());
    };
  }, [auditSetId, i18n.language]);

  console.log("????????????????????????", rootFiles.length);

  return (
    <>
      <Header />
      <div className="flex flex-col lg:flex-row p-0 ">
        {isLoading && (
          <div className="loading-overlay">
            <CircularProgress className="loading-spinner" />
          </div>
        )}

        <div className="flex flex-col border w-full lg:w-3/4 border-blue-500 rounded-xl m-3">
          <div className="bg-blue-500 p-3 text-white font-bold rounded-t-xl">
            {/* <h1>{t("viewItemsUnderAuditScreenSubTitleText")}</h1> */}
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
            <div className="w-full flex justify-between py-1 border px-4">
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
                  {t("viewItemsUnderAuditScreenSubModifiedAtText")}
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
              rootFiles.map((item) => (
                <Node
                  key={item.itemId}
                  item={item}
                  auditSetId={auditSetId}
                  rootId={item.itemId}
                  setSelectedItem={setSelectedItem}
                />
              ))
            )}
          </div>
        </div>

        <Paper className="flex flex-col  justify-between  w-full h-full lg:w-1/4 rounded-xl bg-[#000d2d]">
          {selectedItem && (
            <div
              className="flex flex-col justify-between"
              style={{ backgroundColor: "#000d2d", color: "#fff" }}
            >
              <div>
                <div>
                  {selectedItem.itemType === "file" && (
                    <ExternalPreviewFile
                      selectedItem={selectedItem}
                      auditSetId={auditSetId}
                    />
                  )}
                </div>
              </div>
              <div>
                <div className="flex justify-between mb-0 p-3">
                  <h1 className="text-md font-bold gap-2 flex justify-center items-center cursor-pointer">
                    {selectedItem.itemType === "file" ? (
                      <DescriptionIcon sx={{ color: blue[700] }} />
                    ) : (
                      <FolderIcon sx={{ color: blue[700] }} />
                    )}
                    <PathToolTip text={selectedItem.itemName} />
                  </h1>
                </div>

                {selectedItem.itemType === "file" && (
                  <>
                    <Divider variant="fullWidth" color="#fff" />
                    <Validating item={selectedItem} />
                  </>
                )}

                <Divider variant="fullWidth" color="#fff" />

                <div className="p-3">
                  <h1 className="text-md font-bold">
                    {t("viewItemsUnderAuditScreenSubFileDetailsText")}
                  </h1>
                  <div className="flex flex-col w-full gap-1 mt-2">
                    <div className="flex justify-between">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubCreatedAtText")}
                      </p>
                      {/* <p className="text-[12px]"> */}
                      {<DataAndTime dataAndTime={selectedItem.createdAt} />}
                    </div>
                    <div className="flex justify-between gap-1">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubModifiedAtText")}
                      </p>
                      {/* <p className="text-[12px]"> */}
                      {<DataAndTime dataAndTime={selectedItem.modifiedAt} />}
                      {/* </p> */}
                    </div>
                    <div className="flex justify-between gap-1">
                      <p className="text-sm font-bold">
                        {t("viewItemsUnderAuditScreenSubCreatdByText")}
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
                        <div style={{ width: "45%" }}>
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

                <Divider
                  variant="fullWidth"
                  color="rgba(255, 255, 255, 0.25)"
                />

                {selectedItem.itemType === "file" && (
                  <div className="p-3">
                    <OpenPropertiesDialog
                      handleSelectedOption={handleSelectedOption}
                    />
                  </div>
                )}
              </div>
            </div>
          )}
        </Paper>

        <BootstrapDialog
          onClose={handleClickFileVersionClose}
          aria-labelledby="customized-dialog-title"
          open={fileVersions}
          maxWidth="xl"
        >
          <div className="w-[850px] px-3">
            <div className="flex justify-between items-center px-4">
              {/* <p className="text-xl font-bold ">File Versions</p>
               */}
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
              {fileVersionsData && fileVersionsData.length > 0 ? (
                <Paper
                  style={{
                    overflow: "hidden",
                    height: "480px",
                  }}
                >
                  <TableContainer sx={{ maxHeight: 440 }}>
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
                        {fileVersionsData
                          .slice(
                            page * rowsPerPage,
                            page * rowsPerPage + rowsPerPage
                          )
                          .map((log, index) => (
                            <TableRow key={index}>
                              <TableCell>
                                <span>V</span>
                                {log.itemVersionNumber}
                              </TableCell>
                              <TableCell>
                                <DataAndTime dataAndTime={log.modifiedAt} />
                              </TableCell>
                              <TableCell>{log.sha1Hash}</TableCell>
                            </TableRow>
                          ))}
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
                        labelRowsPerPage={t("tablePaginationTitleText")}
                        count={fileVersionsData.length}
                        page={page}
                        onPageChange={(event, pageNumber) => {
                          handleChangePage(event, pageNumber);
                        }}
                        onChangeRowsPerPage={() => {
                          console.log("onChangeRowsPerPage/////////////////");
                        }}
                        labelDisplayedRows={({ from, to, count }) => {
                          if (i18n.language == "ja") {
                            return `全アイテム  ${count} ${t(
                              "tablePageOffText"
                            )} ${from}-${to}  (${t(
                              "tablePageText"
                            )}  ${Math.ceil(count / rowsPerPage)})`;
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
              ) : (
                <>
                  <TableContainer sx={{ maxHeight: 440 }}>
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
                        <TableRow>
                          <TableCell colSpan={4} align="center">
                            <div
                              style={{
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                                minHeight: "350px", // Adjust height as needed
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
                      </TableBody>
                    </Table>
                  </TableContainer>
                </>
              )}
            </DialogContent>
          </div>
        </BootstrapDialog>

        <BootstrapDialog
          onClose={handleClickFileCopiesClose}
          aria-labelledby="customized-dialog-title"
          open={fileCopies}
          maxWidth="xl"
        >
          <div className="w-[850px] px-3">
            <div className="flex justify-between items-center px-4">
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
              <IconButton
                aria-label="close"
                onClick={handleClickFileCopiesClose}
              >
                <CloseIcon />
              </IconButton>
            </div>
            <DialogContent dividers>
              {Array.isArray(fileCopiesData) && fileCopiesData.length > 0 ? (
                <>
                  <Paper
                    style={{
                      overflow: "hidden",
                      height: "480px",
                    }}
                  >
                    <TableContainer sx={{ maxHeight: 440 }}>
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
                          labelRowsPerPage={t("tablePaginationTitleText")}
                          count={fileCopiesData.length}
                          page={page}
                          onPageChange={(event, pageNumber) => {
                            handleChangePage(event, pageNumber);
                          }}
                          onChangeRowsPerPage={handleChangeRowsPerPage}
                          labelDisplayedRows={({ from, to, count }) => {
                            if (i18n.language == "ja") {
                              return `全アイテム  ${count} ${t(
                                "tablePageOffText"
                              )} ${from}-${to}  (${t(
                                "tablePageText"
                              )}  ${Math.ceil(count / rowsPerPage)})`;
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
                </>
              ) : (
                <>
                  <TableContainer sx={{ maxHeight: 440 }}>
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
                        <TableRow>
                          <TableCell colSpan={4} align="center">
                            <div
                              style={{
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                                minHeight: "350px", // Adjust height as needed
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
                      </TableBody>
                    </Table>
                  </TableContainer>
                </>
              )}
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
              <p></p>
            )}
          </div>
        </BootstrapDialog>
      </div>
    </>
  );
};

export default ExternalAuditorViewitemAnderAuditSet;
