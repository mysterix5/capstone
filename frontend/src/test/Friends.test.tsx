import {MemoryRouter} from "react-router-dom";
import {render, waitFor, screen} from "@testing-library/react";
import axios from "axios";
import Friends from "../pages/userpage/Friends";


test('sending a friendship request', async () => {
    jest.spyOn(axios, 'get').mockImplementation((url: string) => {
        expect(url).toEqual('/api/userdetails/friendsinfo');
        return Promise.resolve({
            data: {
                users: [{username: "user1"}, {username: "user2"}],
                friends: [],
                friendRequests: [],
                friendRequestsReceived: []
            }
        })})

    render(<MemoryRouter><Friends/></MemoryRouter>)

    await waitFor(()=> {
        expect(screen.getByTestId("friendspage")).toHaveTextContent("user1")
        expect(screen.getByTestId("friendspage")).toHaveTextContent("user2")
    })
})