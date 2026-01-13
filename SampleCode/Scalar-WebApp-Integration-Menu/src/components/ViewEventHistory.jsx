import "../css/filehistory.css";
import "../css/customLayout.css";
import CustomLayout from "./CusotmLayout.jsx";
import dayjs from "dayjs";

import React, { useState, useEffect } from "react";
import ClearButton from "./ClearButton.jsx";
import TableList from "./TableList.jsx";
import { useSelector } from "react-redux";
import Header from "./Header.jsx";
import PathToolTip from "./FileDetailsComponet.jsx";
import { CircularProgress } from "@mui/material";
import { BASE_URL } from "../utils/constant.js";
import useAxiosPrivate from "../hooks/useAxiosPrivate.js";
import { useTranslation } from "react-i18next";

const ViewEventHistory = () => {
  const { user } = useSelector((store) => store.auth);
  const {t,i18n} = useTranslation();

  const { itemDetails } = { ...user };
  const { id, path, type, name } = { ...itemDetails };

  const [eventHistoryData, setEventHistoryData] = useState([]);
  const [collbraterList, setItemColbratter] = useState([]);
  const [managedUsersList, setManagedUserList] = React.useState([]);
  const [eventTypeList, setEventTypeList] = useState([]);
  const today = dayjs();
  const yesterday = dayjs().subtract(15, "day");
  const [startDate, setStartDate] = React.useState(yesterday);
  const [endDate, setEndDate] = React.useState(today);
  const [selectedUserName, setSelectedUserName] = useState("");
  const [selectedEventName, setSelectedEventName] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const axiosPrivate = useAxiosPrivate();

  let usernameSet = new Set();
  let usernameList = [];

  if (eventHistoryData) {
    eventHistoryData.forEach((event) => {
      const userObject = {
        userName: event.eventCreatedUserName,
        userId: event.eventCreatedUserId,
      };
      usernameSet.add(JSON.stringify(userObject));
    });
  } else {
    console.error("eventHistoryData is null or undefined");
    usernameList = [];
  }
  const UniqUsernameList = Array.from(usernameSet).map((jsonString) =>
    JSON.parse(jsonString)
  );
  usernameList = UniqUsernameList.filter(
    (user, index, self) =>
      index === self.findIndex((u) => u.userName === user.userName)
  );
  const handleUserNameChange = (newUserName) => {
    setSelectedUserName(newUserName);
  };
  const handleEventNameChange = (newEventName) => {
    setSelectedEventName(newEventName);
  };
  const clearFields = () => {
    setStartDate(null);
    setEndDate(null);
    setSelectedUserName("");
    setSelectedEventName("");
    setEventHistoryData([]);
  };

  const formatDateMethod = (Datee) => {
    const formattedDate = new Intl.DateTimeFormat(undefined, {
      year: "numeric",
      day: "2-digit",
      month: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      fractionalSecondDigits: 3,
    }).format(Datee);

    const formattedDateWithColon = Datee.format("YYYY/MM/DD HH:mm:ss:SSS");
    // Parse the date string using Day.js
    const originalDate = dayjs(formattedDateWithColon);

    // Subtract the time zone offset for IST (5 hours 30 minutes)
    const utcDate = originalDate.subtract(5, "hour").subtract(30, "minute");

    // Format the UTC date according to the desired format
    const formattedUtcDate = utcDate.format("YYYY/MM/DD HH:mm:ss:SSS");

    return formattedUtcDate;
  };

  const FetchEventHistory = (fileItemId, startDateString, endDateString) => {
    const geteventBydate = `${BASE_URL}/box/event/getEventsByDateRangeAndFileId`;
    const url = `${geteventBydate}?startDate=${encodeURIComponent(
      startDateString
    )}&endDate=${encodeURIComponent(endDateString)}&FileId=${fileItemId}`;
    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer " + user.jwtToken,
    // };
    setIsLoading(true);
    axiosPrivate 
      .get(url)
      .then((response) => {
        setEventHistoryData(response.data.data);
      })
      .catch((error) => {
        setEventHistoryData([]);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const FetchEventHistoryByUser = (
    fileItemId,
    startDateString,
    endDateString,
    userIdd
  ) => {
    const geteventByUser = `${BASE_URL}/box/event/getEventsByDateRangeAndUserAndItemId`;

    const url = `${geteventByUser}?startDate=${encodeURIComponent(
      startDateString
    )}&endDate=${encodeURIComponent(
      endDateString
    )}&userId=${userIdd}&itemId=${fileItemId}`;
    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer " + user.jwtToken,
    // };
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventHistoryData(response.data.data);
      })
      .catch((error) => {
        setEventHistoryData([]);
        console.error(error);
      });
  };

  const FetchEventHistoryByEventType = (
    fileItemId,
    startDateString,
    endDateString,
    eventTypeStr
  ) => {
    const geteventByEvent = `${BASE_URL}/box/event/getEventsByDateRangeAndEventTypeAndItemId`;

    const url = `${geteventByEvent}?startDate=${encodeURIComponent(
      startDateString
    )}&endDate=${encodeURIComponent(
      endDateString
    )}&eventType=${eventTypeStr}&itemId=${fileItemId}`;
    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer " + user.jwtToken,
    // };
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventHistoryData(response.data.data);
      })
      .catch((error) => {
        setEventHistoryData([]);
        console.error(error);
      });
  };

  const FetchEventHistoryByEventTypeAndUserId = (
    fileItemId,
    startDate,
    endDate,
    eventTypeStr,
    userIdd
  ) => {
    const geteventByUserandEvent = `${BASE_URL}/box/event/getEventsByDateRangeAndEventTypeAndItemIdAndUserId`;
    const url = `${geteventByUserandEvent}?startDate=${encodeURIComponent(
      startDate
    )}&endDate=${encodeURIComponent(
      endDate
    )}&eventType=${eventTypeStr}&itemId=${fileItemId}&userId=${userIdd}`;

    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer " + user.jwtToken,
    // };
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventHistoryData(response.data.data);
      })
      .catch((error) => {
        setEventHistoryData([]);
        console.error(error);
      });
  };

  const FetchEventType = () => {
    const getEventType = `${BASE_URL}/box/event/getEventType`;
    const url = `${getEventType}`;
    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer " + user.jwtToken,
    // };
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventTypeList(response.data.data);
      })
      .catch((error) => {
        setEventTypeList([]);
        console.error(error);
      });
  };
  //   const getManagedUsers = () => {
  //     const url = `${BASE_URL}/box/user/getManagedUsers/${1098769557}`;
  //     const headers = {
  //       "Content-Type": "application/json",
  //       Authorization: "Bearer " + jwtToken,
  //     };
  //     axios
  //       .get(url, { headers })
  //       .then((response) => {
  //         // console.log(response.data.data);
  //         const userEmails = response.data.data.map((user) => ({
  //           name: user.name,
  //           userId: user.id,
  //         }));
  //         setManagedUserList(userEmails);
  //       })
  //       .catch((error) => {
  //         setManagedUserList([]);
  //         console.error(error);
  //       });
  //   };

  const getManagedUsers = (fileItemId, fileType) => {
    const getCollbratter = `${BASE_URL}/box/file/getItemCollaborator`;

    const url = `${getCollbratter}?itemId=${fileItemId}&itemType=${fileType}`;
    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer " + user.jwtToken,
    // };
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data.data);

        const dataList = combineAndSortData(response.data.data);

        // console.log("this is result",dataList);

        setManagedUserList(dataList);
      })
      .catch((error) => {
        setManagedUserList([]);
        console.error(error);
      });
  };

  ///////////////////////////////////////////////////////////////////////////
  function processDataObject(data) {
    // console.log("data objects dataaaa ",data);
    const { ownerName, onwerId, itemCollaborators } = data;
    const ownerInfo = { name: ownerName, userId: onwerId };

    let resultList = [ownerInfo];

    // console.log("owner info",ownerInfo,resultList);

    if (Array.isArray(itemCollaborators) && itemCollaborators.length > 0) {
      const collaboratorInfo = itemCollaborators.map((collaborator) => ({
        name: collaborator.name,
        userId: collaborator.userId,
      }));
      resultList = resultList.concat(collaboratorInfo);
    }

    return resultList;
  }

  function processDataArray(data) {
    console.log("data array");
    return data.flatMap((item) => {
      const { ownerName, onwerId, itemCollaborators } = item;
      const ownerInfo = { name: ownerName, userId: onwerId };
      let resultList = [ownerInfo];

      if (Array.isArray(itemCollaborators) && itemCollaborators.length > 0) {
        const collaboratorInfo = itemCollaborators.map((collaborator) => ({
          name: collaborator.name,
          userId: collaborator.userId,
        }));
        resultList = resultList.concat(collaboratorInfo);
      }

      return resultList;
    });
  }

  function combineAndSortData(data) {
    let dataList = [];

    if (Array.isArray(data)) {
      dataList = processDataArray(data);
    } else if (typeof data === "object") {
      dataList = processDataObject(data);
    } else {
      console.error("Unsupported data format");
      return;
    }

    dataList.sort((a, b) => a.userId.localeCompare(b.userId));
    return dataList;
  }

  /////////////////////////////////////////////////////////////////////////
  useEffect(() => {
    FetchEventHistory(
      id,
      formatDateMethod(startDate),
      formatDateMethod(endDate)
    );
    FetchEventType();
    getManagedUsers(id, type);
    // getManagedUsers();
  }, []);

  useEffect(() => {
    // Your code here
    // console.log('startDate:', formatDateMethod(startDate));
    // console.log('endDate:', formatDateMethod(endDate));
    // console.log('selectedUserName:', selectedUserName);
    // console.log('selectedEventName:', selectedEventName);

    if (
      startDate !== null &&
      endDate !== null &&
      selectedUserName === "" &&
      selectedEventName === ""
    ) {
      FetchEventHistory(
        id,
        formatDateMethod(startDate),
        formatDateMethod(endDate)
      );
    } else if (
      startDate !== null &&
      endDate !== null &&
      selectedUserName !== "" &&
      selectedEventName === ""
    ) {
      FetchEventHistoryByUser(
        id,
        formatDateMethod(startDate),
        formatDateMethod(endDate),
        selectedUserName
      );
    } else if (
      startDate !== null &&
      endDate !== null &&
      selectedUserName === "" &&
      selectedEventName !== ""
    ) {
      FetchEventHistoryByEventType(
        id,
        formatDateMethod(startDate),
        formatDateMethod(endDate),
        selectedEventName
      );
    } else if (
      startDate !== null &&
      endDate !== null &&
      selectedUserName !== "" &&
      selectedEventName !== ""
    ) {
      FetchEventHistoryByEventTypeAndUserId(
        id,
        formatDateMethod(startDate),
        formatDateMethod(endDate),
        selectedEventName,
        selectedUserName
      );
    }
  }, [startDate, endDate, selectedUserName, selectedEventName]);

  return (
    <div>
      <Header />
      <div className="file-information-container">
        {/* <div className="folder-name-text">File name: {name}</div>
        <div className="file-path-name-text">File Path: {path}</div> */}

        <div
          style={{
            display: "flex",
            flexDirection: "row",
            width: "100%",
            justifyContent: "start",
            alignItems: "center",
          }}
        >
          <PathToolTip text={name} title={t("rightClickMainFileNameText")} />
          <div style={{ width: "10%" }}></div>
          <PathToolTip text={path} title={t("rightClickMainFilePathText")} />
        </div>
      </div>
      <div className="white-line"></div>
      <div className="file-information-container">
        <div className="filter-text">{t("viewAllEventHistoryScreenFilterText")}</div>

        <div style={{ height: "20px", width: "20px" }}></div>

        <div className="custom-lable-layout-container-one" data-content={t("viewAllEventHistoryScreenStartDateText")}>
          <CustomLayout
            typeContainer={"DatePicker"}
            value={startDate}
            setValue={setStartDate}
          />
        </div>

        <div style={{ height: "20px", width: "20px" }}></div>

        <div className="custom-lable-layout-container-two" data-content={t("viewAllEventHistoryScreenEndDateText")}>
          <CustomLayout
            typeContainer={"DatePicker"}
            value={endDate}
            setValue={setEndDate}
          />
        </div>

        <div style={{ height: "20px", width: "20px" }}></div>

        <div className="custom-lable-layout-container-three" data-content={t("viewAllEventHistoryScreenUsernameText")}>
          <CustomLayout
            firstList={true}
            dropdownList={managedUsersList}
            selectedOption={selectedUserName}
            onDropdownChange={handleUserNameChange}
          />
        </div>

        <div style={{ height: "20px", width: "20px" }}></div>
        <div className="custom-lable-layout-container-four" data-content={t("viewAllEventHistoryScreenEventTypeText")}>
          <CustomLayout
            firstList={false}
            dropdownListTwo={eventTypeList}
            selectedOption={selectedEventName}
            onDropdownChange={handleEventNameChange}
          />
        </div>

        {/* <div style={{ height: "20px", width: "20px" }}></div> */}

        {/* <button
          style={{
            width: 80,
            cursor: "pointer",
            border: "none",
            backgroundColor: "white",
          }}
          onClick={viewAllHandle}
        >
          View All
        </button> */}

        <div style={{ height: "20px", width: "20px" }}></div>
        <ClearButton onClick={clearFields} />
      </div>

      {isLoading ? (
        <div
          className="loading-overlay "
          style={{
            alignItems: "center",
            justifyContent: "center",
            display: "flex",
            height: "60vh",
          }}
        >
          <CircularProgress className="loading-spinner" />
        </div>
      ) : (
        <TableList dataList={eventHistoryData} />
      )}
    </div>
  );
};

export default ViewEventHistory;
