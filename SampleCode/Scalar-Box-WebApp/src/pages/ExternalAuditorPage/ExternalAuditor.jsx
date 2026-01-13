import React, { useEffect, useRef, useState } from "react";
import Header from "../../common/Header/Header";
import { BASE_URL } from "../../utils/constants";
import {
  Box,
  IconButton,
  Paper,
  Rating,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  Tooltip,
  Typography,
  tableCellClasses,
} from "@mui/material";

import Description from "@mui/icons-material/Description";
import styled from "@emotion/styled";

import ExternalAuditorCard from "./ExternalAuditorCard";
import { useNavigate } from "react-router-dom";
import { CARDS_KEYS } from "../../utils/constants";
import Loder from "../../common/Loader/Loder";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#0061D5",
    color: "#fff",
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: "100px",
  },
}));

const ExternalAuditor = () => {
  const axiosPrivate = useAxiosPrivate();
  const [respoData, setResData] = useState([]);

  const [isLoading, setIsLoading] = useState(true);
  const [loadAgain, setLoadAgain] = useState(true);
  const [selectedRows, setSelectedRows] = useState([]);
  const [selectedId, setSelectedId] = useState(CARDS_KEYS[0]);
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);

  const {t,i18n} = useTranslation();
  const user = useSelector((state) => state.auth.user);

  const useRefMap = useRef(new Map());

  const titleMap = new Map();
  titleMap.set(CARDS_KEYS[0], t("externalAuditorScreenAssignAuditSetText"));
  titleMap.set(CARDS_KEYS[1], t("externalAuditorScreenAuditSetsUnderReview"));
  titleMap.set(CARDS_KEYS[2], t("externalAuditorScreenNewlyAddedAuditSetText"));

  const selectedCard = (selectedKey) => {
    setSelectedId(selectedKey);
    setResData(useRefMap.current.get(selectedKey));
  };

  const navigate = useNavigate();

  // const toggleIsFavourite = async (auditSetId, status) => {
  //   const headers = new Headers();
  //   headers.append("Content-Type", "application/json");
  //   headers.append(
  //     "Authorization",
  //     `Bearer ${encodeURIComponent(user.)}`
  //   );

  //   const url = `${BASE_URL}/box/auditSetCollab/markIsFavouriteAuditSet?auditSetId=${encodeURIComponent(
  //     auditSetId
  //   )}&status=${encodeURIComponent(status)}`;
  //   // setIsLoading(true);
  //   fetch(url, {
  //     method: "PUT",
  //     headers: headers,
  //   })
  //     .then((response) => {
  //       if (response.status) {
  //         setLoadAgain((loadAgain) => !loadAgain);
  //       } else {
  //         throw Error("");
  //       }
  //     })
  //     .catch((error) => {
  //       console.error("/////////ERROR :: ", error);
  //     })
  //     .finally(() => {});
  // };

  useEffect(() => {
    async function fetchAuditSet() {
      useRefMap.current.set(CARDS_KEYS[0], []);
      useRefMap.current.set(CARDS_KEYS[1], []);
      useRefMap.current.set(CARDS_KEYS[2], []);
      // useRefMap.current.set(CARDS_KEYS[3], []);

      axiosPrivate
        .get("/box/auditSet/getMyAuditSetList")
        .then((response) => {
          console.log("RRRRRRRRRRRR", response);

          if (response.status === 200) {
            const underReview = [...response.data.data].filter(
              (item) => item.accessStatus === CARDS_KEYS[1]
            );
            const newlyAdded = [...response.data.data].filter(
              (item) => item?.accessStatus === CARDS_KEYS[2]
            );

            const favourites = [...response.data.data].filter((item) => {
              console.log("favourites", item);
              return item.isFavourite === true;
            });

            useRefMap.current.set(CARDS_KEYS[0], [...response.data.data]);
            useRefMap.current.set(CARDS_KEYS[1], underReview);
            useRefMap.current.set(CARDS_KEYS[2], newlyAdded);
            useRefMap.current.set(CARDS_KEYS[3], favourites);
            setResData(useRefMap.current.get(selectedId));
          }
        })
        .catch((error) => {
          // TODO : HANDLE ERROR
          console.error("ERROR : ", error);
        });
    }

    setIsLoading(true);
    fetchAuditSet().finally(() => {
      setIsLoading(false);
    });
  }, [loadAgain,i18n.language]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };
  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const handleDescriptionIconClick = (auditSetId, auditSetName) => {
    const encodedAuditSetName = encodeURIComponent(auditSetName);
    navigate(`/external/viewitemunderauditset/${auditSetId}/${auditSetName}`);
  };

  if (isLoading) {
    return <Loder />;
  }

  
  return (
    <>
      <Header />
      <div className="px-5 py-2">
        <p style={{ fontSize: "26px", fontWeight: "700" }}>
          {t("externalAuditorScreenTitleText")}
        </p>
        <div className="py-2" style={{ display: "flex", gap: "30px",whiteSpace:"nowrap" }}>
          {CARDS_KEYS.map((key, index) => {
            const size = useRefMap.current.get(key).length;
            return (
              <div key={key}>
                <ExternalAuditorCard
                  key={key}
                  id={key}
                  count={size}
                  selectedId={selectedId}
                  setSelected={selectedCard}
                  title={titleMap.get(key)}
                />
              </div>
            );
          })}
        </div>
        <div>
          <Paper
            sx={{ width: "100%", overflow: "hidden", borderRadius: "20px" }}
          >
            <TableContainer sx={{ maxHeight: 500 }}>
              <Table stickyHeader aria-label="sticky table">
                <TableHead>
                  <TableRow>
                    <StyledTableCell
                      style={{
                        paddingLeft: "42px",
                        fontSize: "18px",
                        width: "40%",
                      }}
                    >
                      {t("externalAuditorScreenAuditSetNameText")}
                    </StyledTableCell>
                    <StyledTableCell style={{ fontSize: "18px", width: "50%" }}>
                      {t("externalAuditorScreenAuditDescriptionText")}
                    </StyledTableCell>
                    <StyledTableCell style={{ fontSize: "18px", width: "10%" }}>
                      {t("externalAuditorScreenActionText")}
                    </StyledTableCell>
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

                          console.log(
                            "SELECTED :: ",
                            row.auditSetId,
                            row.isFavourite,
                            row
                          );
                          return (
                            <TableRow
                              hover
                              role="checkbox"
                              tabIndex={-1}
                              key={row.auditSetId}
                              // selected={isSelected}
                            >
                              <TableCell
                                style={{
                                  height: "10px",
                                  wordWrap: "break-word",
                                  paddingLeft: "15px",
                                  alignItems: "center",
                                }}
                              >
                                <div style={{ paddingLeft: "25px" }}>
                                  {/* <Rating
                                    name="size-large"
                                    value={row.isFavourite ? 1 : 0}
                                    max={1}
                                    onChange={(target) => {
                                      toggleIsFavourite(
                                        row.auditSetId,
                                        row.isFavourite
                                      );
                                    }}
                                    style={{ color: "rgba(0, 97, 213, 0.8)" }}
                                  /> */}

                                  <Typography>{row.auditSetName}</Typography>
                                </div>
                              </TableCell>
                              <TableCell
                                style={{
                                  wordWrap: "break-word",
                                }}
                              >
                                {row.description}
                              </TableCell>
                              <TableCell style={{}}>
                                <Tooltip title={t("manageAuditSetScreenViewItemsButtonText")} arrow>
                                  <IconButton
                                    style={{
                                      height: "5px",
                                    }}
                                    onClick={() => {
                                      handleDescriptionIconClick(
                                        row.auditSetId,
                                        row.auditSetName
                                      );
                                    }}
                                  >
                                    <Description />
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
      </div>
    </>
  );
};

export default ExternalAuditor;
