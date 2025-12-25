import "./ViewAllEventHistory.css";
import TwoCustomLayout from "./components/TwoCustomLayout";
import React, { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import dayjs from "dayjs";
import TableList from "./components/TableList";
import ClearButton from "./components/ClearButton";
import { BASE_URL } from "../../utils/constants";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

const ViewAllEventsHistoryByItem = ({
  auditsetId,
  FileItemId,
  FileType = "file",
}) => {
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
  const axiosPrivate = useAxiosPrivate();

  const { t, i18n } = useTranslation();

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

  const getManagedUsers = (fileItemId, fileType) => {
    const getCollbratter = `${BASE_URL}/box/file/getItemCollaborator`;

    const url = `${getCollbratter}?itemId=${fileItemId}&itemType=${fileType}&auditSetId=${auditsetId}`;

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
    const { ownerName, onwerId, itemCollaborators } = data;
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
  }

  function processDataArray(data) {
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
      FileItemId,
      formatDateMethod(startDate),
      formatDateMethod(endDate)
    );
    FetchEventType();
    getManagedUsers(FileItemId, FileType);
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
        FileItemId,
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
        FileItemId,
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
        FileItemId,
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
        FileItemId,
        formatDateMethod(startDate),
        formatDateMethod(endDate),
        selectedEventName,
        selectedUserName
      );
    }
  }, [startDate, endDate, selectedUserName, selectedEventName]);
  return (
    <div>
      <div style={{ height: "10px" }}></div>
      <div className="white-line"></div>
      <div style={{ height: "20px" }}></div>
      <div className="file-information-container">
        <div className="filter-text">
          {t("viewAllEventHistoryScreenFilterText")}
        </div>
        <div style={{ height: "20px", width: "20px" }}></div>
        <div
          className="custom-lable-start-date"
          data-content={t("viewAllEventHistoryScreenStartDateText")}
        >
          <TwoCustomLayout
            typeContainer={"DatePicker"}
            value={startDate}
            setValue={setStartDate}
          />
        </div>
        <div style={{ height: "20px", width: "20px" }}></div>
        <div
          className="custom-lable-end-date"
          data-content={t("viewAllEventHistoryScreenEndDateText")}
        >
          <TwoCustomLayout
            typeContainer={"DatePicker"}
            value={endDate}
            setValue={setEndDate}
          />
        </div>
        <div style={{ height: "20px", width: "20px" }}></div>
        <div
          className="custom-lable-username"
          data-content={t("viewAllEventHistoryScreenUsernameText")}
        >
          <TwoCustomLayout
            firstList={true}
            dropdownList={managedUsersList}
            selectedOption={selectedUserName}
            onDropdownChange={handleUserNameChange}
          />
        </div>
        <div style={{ height: "20px", width: "20px" }}></div>
        <div
          className="custom-lable-event-type"
          data-content={t("viewAllEventHistoryScreenEventTypeText")}
        >
          <TwoCustomLayout
            firstList={false}
            dropdownListTwo={eventTypeList}
            selectedOption={selectedEventName}
            onDropdownChange={handleEventNameChange}
          />
        </div>
        <div style={{ height: "20px", width: "20px" }}></div>
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
      <div style={{ height: "10px" }}></div>
      <TableList dataList={eventHistoryData} />
      {/* <div>{FileItemId}</div> */}
    </div>
  );
};
export default ViewAllEventsHistoryByItem;
