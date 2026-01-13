/* eslint-disable react/prop-types */
import "./Root.css";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import FileIcon from "../../Icons/FileIcon";
import FolderIcon from "../../Icons/FolderIcon";
import { BASE_URL } from "../../utils/constant";
import { Checkbox, FormControlLabel } from "@mui/material";
import { addToDenaiList, removeFromDenaiList } from "./auditFolderSlice";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

export default function Node({ item, isRootAllowed, subRootItemId }) {
  const { user } = useSelector((store) => store.auth);
  const [itemDetails, setItemdetails] = useState([]);
  const dispatch = useDispatch();
  const { allowedList, partiallyAllowedItems } = useSelector(
    (store) => store.folder
  );

  const axiosPrivate = useAxiosPrivate();
  const subRoot = allowedList.find((item) => item.itemId === subRootItemId);
  let isSubRootAvailable = subRoot ? true : false;

  console.log(subRoot, "pppppppppp", item.id);
  // console.log(
  //   "pppppppppppppp",
  //   subRoot.denyItems.find((i) => i.itemId === item.id)
  // );
  let isAllowed = isSubRootAvailable && false;

  if (item.name === "Folder One") {
    console.log("************************************************");
    console.log("allowedList :: ", allowedList);

    console.log("NODE SUBROOT :: ", subRoot, allowedList);
    console.log(
      "NODE :: ",
      item.name,
      "ROOT-ALLOWED :: ",
      isRootAllowed,
      "subRootItemId :: ",
      subRootItemId,
      "isSubRootAvailable :: ",
      isSubRootAvailable
    );
    console.log("************************************************");
  }

  async function fetchItem() {
    if (item.type === "file") {
      return;
    }
    console.log("before fetchItem");
    if (itemDetails.length <= 0) {
      console.log("fetchItem", item.id, user.jwtToken);
      // const headers = new Headers();
      // headers.append("Content-Type", "application/json");
      // headers.append(
      //   "Authorization",
      //   `Bearer ${encodeURIComponent(user.jwtToken)}`
      // );

      const url = `${BASE_URL}/box/folder/getItemList?folderId=${encodeURIComponent(
        item.id
      )}`;
      const response = await axiosPrivate.get(url);
      // const response = await fetch(url, {
      //   method: "GET",
      //   headers: headers,
      // });

      if (response.status === 200) {
        const { folderDetailsDtoList, size } = { ...response.data.data };
        console.log(
          "folderDetailsDtoList :: ",
          folderDetailsDtoList,
          "SIZE  :: ",
          size
        );
        console.log(response.data);
        setItemdetails([...folderDetailsDtoList]);
      }
      // setItemdetails("ss");
    }
  }

  let checked = [false, false];
  return (
    <>
      <div className="node-item">
        {item.type === "folder" ? (
          <FolderIcon className="folder" style={{ color: "#0061D5" }} />
        ) : (
          <FileIcon className="folder" />
        )}
        {item.type === "folder" ? (
          <details open={false} onClick={fetchItem}>
            <summary>
              <FormControlLabel
                label={`${item.name}`}
                control={
                  <Checkbox
                    checked={isAllowed}
                    // indeterminate={checked[0] !== checked[1]}
                    onChange={(e) => {
                      if (e.target.checked) {
                        dispatch(addToDenaiList(item, subRootItemId));
                      } else {
                        dispatch(removeFromDenaiList(item, subRootItemId));
                      }
                    }}
                  />
                }
              />
            </summary>

            {itemDetails && (
              <div className="align-right">
                <ul>
                  {itemDetails.map((item, index) => {
                    return (
                      <li key={index}>
                        <Node
                          item={item}
                          isRootAllowed={true}
                          subRootItemId={subRootItemId}
                        />
                      </li>
                    );
                  })}
                </ul>
              </div>
            )}
          </details>
        ) : (
          <FormControlLabel
            label={`${item.name}`}
            control={
              <Checkbox
                checked={isSubRootAvailable}
                // indeterminate={checked[0] !== checked[1]}
                onChange={(e) => {}}
              />
            }
          />
        )}
      </div>
    </>
  );
}

//back up
/* eslint-disable react/prop-types */
// import "./Root.css";
// import { useState } from "react";
// import { useDispatch, useSelector } from "react-redux";
// import FileIcon from "../../Icons/FileIcon";
// import FolderIcon from "../../Icons/FolderIcon";
// import { BASE_URL } from "../../utils/constant";
// import { Checkbox } from "@mui/material";

// export default function Node({ item, isRootAllowed, isRoot, rootItemId }) {
//   const { user } = useSelector((store) => store.auth);
//   const [itemDetails, setItemdetails] = useState([]);
//   const dispatch = useDispatch();
//   const { deniedList } = useSelector((store) => store.folder);

//   let isDenied = [...deniedList].filter((o) => o.itemId === item.id).length;
//   isDenied = isRootAllowed && !isDenied;

//   // if (isRootAllowed) {
//   //   // isDenied = [...deniedList].filter((o) => o.itemId === item.id).length;
//   //   console.log("IF IF IF ", isRootAllowed);
//   // } else {
//   //   isDenied = false;
//   //   console.log("ELSE ELSE ");
//   // }

//   // isDenied = !isRootAllowed && isDenied;
//   // if (item.type === "folder" && isRootAllowed) {
//   //   isDenied = [...deniedList].filter((o) => o.itemId === item.id).length;
//   // } else {
//   //   isDenied = !isRootAllowed;
//   // }
//   // if (isRootAllowed) {
//   //   console.log("IF IF IF ");
//   //   isDenied = [...deniedList].filter((o) => o.itemId === item.id).length;
//   // } else {
//   //   isDenied = isRootAllowed;
//   // }

//   if (item.name === "DEMO Two") {
//     console.log(
//       "NAME :: ",
//       item.name,
//       "ROOT-ALLOWED :: ",
//       isRootAllowed,
//       "IS-DENIED :: ",
//       isDenied
//     );
//   }

//   async function fetchItem() {
//     if (item.type === "file") {
//       return;
//     }
//     console.log("before fetchItem");
//     if (itemDetails.length <= 0) {
//       console.log("fetchItem", item.id, user.jwtToken);
//       const headers = new Headers();
//       headers.append("Content-Type", "application/json");
//       headers.append(
//         "Authorization",
//         `Bearer ${encodeURIComponent(user.jwtToken)}`
//       );

//       const url = `${BASE_URL}/box/folder/getItemList?folderId=${encodeURIComponent(
//         item.id
//       )}`;
//       const response = await fetch(url, {
//         method: "GET",
//         headers: headers,
//       });

//       if (response.ok) {
//         const jsonData = await response.json();
//         console.log(jsonData.data);
//         setItemdetails([...jsonData.data]);
//       }
//       // setItemdetails("ss");
//     }
//   }

//   return (
//     <>
//       <div className="node-item">
//         {item.type === "folder" ? (
//           <FolderIcon className="folder" style={{ color: "#0061D5" }} />
//         ) : (
//           <FileIcon className="folder" />
//         )}
//         <details open={false} onClick={fetchItem}>
//           <summary>
//             {!isRoot && (
//               <Checkbox
//                 checked={isDenied}
//                 onChange={() => {
//                   if (!isRootAllowed) return;
//                   const ids = [];
//                   if (item.type !== "file") {
//                     console.log("itemDetails", itemDetails);
//                     [...itemDetails].forEach((item) => {
//                       ids.push(item.id);
//                     });
//                   }

//                   // console.log("updateDeniedList");
//                   dispatch({
//                     type: "auditFolder/toggleDenaiList",
//                     payload: {
//                       id: item.id,
//                       itemType: item.type,
//                       rootItemId,
//                       ids,
//                     },
//                   });
//                 }}
//               />
//             )}
//             {/* {!isRoot && (
//               <button
//                 style={{
//                   width: "30px",
//                   height: "40px",
//                   fontSize: "20px",
//                   fontWeight: "bold",
//                 }}
//                 onClick={() => {
//                   if (!isRootAllowed) return;
//                   const ids = [];
//                   if (item.type !== "file") {
//                     console.log("itemDetails", itemDetails);
//                     [...itemDetails].forEach((item) => {
//                       ids.push(item.id);
//                     });
//                   }

//                   // console.log("updateDeniedList");
//                   dispatch({
//                     type: "auditFolder/toggleDenaiList",
//                     payload: {
//                       id: item.id,
//                       itemType: item.type,
//                       rootItemId,
//                       ids,
//                     },
//                   });
//                 }}
//               >
//                 {!isDenied ? <>&times;</> : <>&#x2713;</>}
//               </button>
//             )} */}

//             {item.name}
//           </summary>

//           {itemDetails && (
//             <div className="align-right">
//               <ul>
//                 {itemDetails.map((item, index) => {
//                   return (
//                     <li key={index}>
//                       <Node
//                         item={item}
//                         isRootAllowed={isDenied}
//                         isRoot={false}
//                         rootItemId={item.id}
//                       />
//                     </li>
//                   );
//                 })}
//               </ul>
//             </div>
//           )}
//         </details>
//       </div>
//     </>
//   );
// }
