// import { BASE_URL } from "../utils/constant";

// export const getVersionHistory = async (fileId, token) => {
//   const getFileVersion = `${BASE_URL}/box/file/getFileVersions?fileId=${fileId}`;
//   const response = await fetch(`${getFileVersion}`, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: `Bearer ${token}`,
//     },
//   });
//   return response;
// };

// export const getFileCopy = async (fileId, sha1, token) => {
//   const getfilecopies = `${BASE_URL}/box/file/getFileCopies?sha1Hash=${sha1}&itemId=${fileId}`;
//   const response = await fetch(`${getfilecopies}`, {
//     method: "GET",

//     headers: {
//       "Content-Type": "application/json",
//       Authorization: `Bearer ${token}`,
//     },
//   });
//   return response;
// };

// export const addAuditSet = async (
//   token,
//   auditSetId,
//   itemId,
//   itemName,
//   itemType,
//   createdAt
// ) => {
//   const addAuditSet_url = `${BASE_URL}/box/auditSetItem/addItemToAuditSet/${auditSetId}`;
//   const requestData = {
//     itemId: itemId,
//     itemType: itemType,
//     itemName: itemName,
//     createdAt: createdAt,
//   };
//   const response = await fetch(`${addAuditSet_url}`, {
//     method: "POST",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: `Bearer ${token}`,
//     },
//     body: JSON.stringify(requestData),
//   });
//   return response;
// };

// export const getAuditSetList = async (token, itemId) => {
//   const getAuditList = `${BASE_URL}/box/auditSet/getMyAuditSetListForItemId/${itemId}`;
//   const response = await fetch(`${getAuditList}`, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: `Bearer ${token}`,
//     },
//   });
//   return response;
// };

// export const getFileCopy2 = async (token) => {
//   const getFileProperty = `${BASE_URL}/box/file/getFileDetails?itemId=1415608897455`;
//   const response = await fetch(`${getFileProperty}`, {
//     method: "GET",
//     headers: {
//       "Content-Type": "application/json",
//       Authorization: `Bearer ${token}`,
//     },
//   });
//   return response;
// };
