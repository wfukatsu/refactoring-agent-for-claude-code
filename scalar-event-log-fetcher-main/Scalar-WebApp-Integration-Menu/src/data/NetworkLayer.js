// import { BASE_URL } from "../utils/constant";

// export const FetchEventHistory = async (fileId, startDate, endDate, token) => {
//   const geteventBydate = `${BASE_URL}/box/event/getEventsByDateRangeAndFileId`;

//   const url = `${geteventBydate}?startDate=${encodeURIComponent(
//     startDate
//   )}&endDate=${encodeURIComponent(endDate)}&FileId=${fileId}`;

//   const response = await fetch(url, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: "Bearer " + token,
//     },
//   });
//   console.log("3");
//   const data = response;
//   return data;
// };

// export const FetchItemCollbrater = async (fileItemId, fileType, token) => {
//   const getCollbratter = `${BASE_URL}/box/file/getItemCollaborator`;

//   const url = `${getCollbratter}?itemId=${fileItemId}&itemType=${fileType}`;

//   const response = await fetch(url, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: "Bearer " + token,
//     },
//   });
//   console.log("3");
//   const data = response;
//   return data;
// };

// export const FetchEventType = async (token) => {
//   const getEventsUrl = `${BASE_URL}/box/event/getEventType`;

//   const url = `${getEventsUrl}`;

//   const response = await fetch(url, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: "Bearer " + token,
//     },
//   });
//   console.log("3");
//   const data = response;
//   return data;
// };

// export const FetchEventHistorybyUser = async (
//   startDate,
//   endDate,
//   userIdd,
//   fileId,
//   token
// ) => {
//   const geteventByUser = `${BASE_URL}/box/event/getEventsByDateRangeAndUserAndItemId`;

//   const url = `${geteventByUser}?startDate=${encodeURIComponent(
//     startDate
//   )}&endDate=${encodeURIComponent(endDate)}&userId=${userIdd}&itemId=${fileId}`;

//   const response = await fetch(url, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: "Bearer " + token,
//     },
//   });
//   console.log("3");
//   const data = response;
//   return data;
// };

// export const FetchEventHistorybyEvent = async (
//   startDate,
//   endDate,
//   eventTypeStr,
//   fileId,
//   token
// ) => {
//   const geteventByEvent = `${BASE_URL}/box/event/getEventsByDateRangeAndEventTypeAndItemId`;

//   const url = `${geteventByEvent}?startDate=${encodeURIComponent(
//     startDate
//   )}&endDate=${encodeURIComponent(
//     endDate
//   )}&eventType=${eventTypeStr}&itemId=${fileId}`;

//   const response = await fetch(url, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: "Bearer " + token,
//     },
//   });
//   console.log("3");
//   const data = response;
//   return data;
// };

// export const FetchEventHistorybyUserandEvent = async (
//   startDate,
//   endDate,
//   eventTypeStr,
//   userIdd,
//   fileId,
//   token
// ) => {
//   const geteventByUserandEvent = `${BASE_URL}/box/event/getEventsByDateRangeAndEventTypeAndItemIdAndUserId`;

//   const url = `${geteventByUserandEvent}?startDate=${encodeURIComponent(
//     startDate
//   )}&endDate=${encodeURIComponent(
//     endDate
//   )}&eventType=${eventTypeStr}&itemId=${fileId}&userId=${userIdd}`;

//   const response = await fetch(url, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: "Bearer " + token,
//     },
//   });
//   console.log("3");
//   const data = response;
//   return data;
// };
