// import "./ViewAllEventHistory.css";
import CustomLayout from "./components/CustomLayout";
import React, { useState, useEffect } from "react";
import dayjs from "dayjs";
import TableList from "./components/TableList";
import ClearButton from "./components/ClearButton";
import { BASE_URL } from "../../utils/constants";

import CircularProgress from "@mui/joy/CircularProgress";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

const ViewAllEventHisotry = ({ open }) => {
  const [eventHistoryData, setEventHistoryData] = useState([]);
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
    setLoading(true);
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

    const originalDate = dayjs(formattedDateWithColon);

    // Subtract the time zone offset for IST (5 hours 30 minutes)
    const utcDate = originalDate.subtract(5, "hour").subtract(30, "minute");

    // Format the UTC date according to the desired format
    const formattedUtcDate = utcDate.format("YYYY/MM/DD HH:mm:ss:SSS");

    return formattedUtcDate;

    // return formattedDateWithColon;
  };

  const FetchEventHistory = (startDate, endDate) => {
    const geteventBydate = `${BASE_URL}/box/event/getEventsByDateRange`;
    const url = `${geteventBydate}?startDate=${encodeURIComponent(
      startDate
    )}&endDate=${encodeURIComponent(endDate)}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventHistoryData(response.data.data);
        setLoading(true);
      })
      .catch((error) => {
        setEventHistoryData([]);
        console.error(error);
      });
  };
  const FetchEventHistoryByUser = (startDate, endDate, useriddd) => {
    const geteventBydateandUser = `${BASE_URL}/box/event/getEventsByDateRangeAndUser`;
    const url = `${geteventBydateandUser}?startDate=${encodeURIComponent(
      startDate
    )}&endDate=${encodeURIComponent(endDate)}&userId=${useriddd}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventHistoryData(response.data.data);
        setLoading(true);
      })
      .catch((error) => {
        setEventHistoryData([]);
        console.error(error);
      });
  };
  const FetchEventHistoryByEventType = (startDate, endDate, eventType) => {
    const geteventBydateandEventType = `${BASE_URL}/box/event/getEventsByDateRangeAndEventType`;
    const url = `${geteventBydateandEventType}?startDate=${encodeURIComponent(
      startDate
    )}&endDate=${encodeURIComponent(endDate)}&eventType=${eventType}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventHistoryData(response.data.data);
        setLoading(true);
      })
      .catch((error) => {
        setEventHistoryData([]);
        console.error(error);
      });
  };
  const FetchEventHistoryByEventTypeAndUserId = (
    startDate,
    endDate,
    eventType,
    userIdd
  ) => {
    const geteventBydateByEventTypeandUserId = `${BASE_URL}/box/event/getEventsByDateRangeAndEventTypeAndUser`;
    const url = `${geteventBydateByEventTypeandUserId}?startDate=${encodeURIComponent(
      startDate
    )}&endDate=${encodeURIComponent(
      endDate
    )}&eventType=${eventType}&userId=${userIdd}`;

    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data);
        setEventHistoryData(response.data.data);
        setLoading(true);
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
  const getManagedUsers = () => {
    const url = `${BASE_URL}/box/user/getManagedUsers/${1098769557}`;
    axiosPrivate
      .get(url)
      .then((response) => {
        // console.log(response.data.data);
        const userEmails = response.data.data.map((user) => ({
          name: user.name,
          userId: user.id,
        }));
        setManagedUserList(userEmails);
      })
      .catch((error) => {
        setManagedUserList([]);
        console.error(error);
      });
  };
  /////////////////////////////////////////////////////////////////////////
  useEffect(() => {
    // FetchEventHistory(formatDateMethod(startDate), formatDateMethod(endDate));
    FetchEventType();
    getManagedUsers();
  }, []);

  useEffect(() => {
    // Your code here
    if (startDate != null && endDate != null) {
      setLoading(false);
    }

    if (startDate != null && endDate != null) {
      const formattedDateResultOne = formatDateMethod(startDate);
      const formattedDateResultTwo = formatDateMethod(endDate);
      console.log("start date =", formattedDateResultOne);
      console.log("end date =", formattedDateResultTwo);
    }
    // console.log('startDate:', startDate);
    // console.log('endDate:', endDate);
    console.log("selectedUserName:", selectedUserName);
    console.log("selectedEventName:", selectedEventName);

    // Example: Fetch data or perform actions based on the changes in these variables
    if (
      startDate !== null &&
      endDate !== null &&
      selectedUserName === "" &&
      selectedEventName === ""
    ) {
      FetchEventHistory(formatDateMethod(startDate), formatDateMethod(endDate));
    } else if (
      startDate !== null &&
      endDate !== null &&
      selectedUserName !== "" &&
      selectedEventName === ""
    ) {
      FetchEventHistoryByUser(
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
        formatDateMethod(startDate),
        formatDateMethod(endDate),
        selectedEventName,
        selectedUserName
      );
    }
  }, [startDate, endDate, selectedUserName, selectedEventName]);

  const [loading, setLoading] = useState(false);

  return (
    <div style={{ width: "100%" }}>
      <div className="file-information-container">
        <h1 className="font-bold text-xl">
          {t("viewAllEventHistoryScreenTitleText")}
        </h1>
      </div>
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
          <CustomLayout
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
          <CustomLayout
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
          <CustomLayout
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
          <CustomLayout
            firstList={false}
            dropdownListTwo={eventTypeList}
            selectedOption={selectedEventName}
            onDropdownChange={handleEventNameChange}
          />
        </div>
        <div style={{ height: "20px", width: "20px" }}></div>

        <div style={{ height: "20px", width: "20px" }}></div>
        <ClearButton onClick={clearFields} />
      </div>
      <div style={{ height: "10px" }}></div>
      {loading ? (
        <TableList dataList={eventHistoryData} />
      ) : (
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
      )}
    </div>
  );
};
export default ViewAllEventHisotry;
